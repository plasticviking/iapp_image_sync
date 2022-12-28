package com.plasticviking.iappsync.services;

import com.amazonaws.services.s3.AmazonS3;
import com.plasticviking.iappsync.configuration.ObjectStore;
import com.plasticviking.iappsync.configuration.ObjectStoreConfigurationProperties;
import com.plasticviking.iappsync.data.ShallowIAPPRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.stream.Collectors;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringValueResolver;

@Component
public class InvasivesInterface {

	private Logger log = LoggerFactory.getLogger(InvasivesInterface.class.getCanonicalName());

	private Set<String> seenMimeTypes = new HashSet<>();

	@Autowired
	@Qualifier("InvasivesJdbcTemplate")
	private JdbcTemplate template;

	@Autowired
	private Tika tikaInstance;

	@Autowired
	private ObjectStoreInterface objectStoreInterface;

	public void dumpSeenTypes() {
		log.info("Dumping mime types we have observed in this run:");
		log.info(String.join(", ", seenMimeTypes));
	}

	private static String getExtension(String mimeType) {
		return switch (mimeType) {
			case "image/png" -> "png";
			case "image/jpeg" -> "jpg";
			case "image/jpg" -> "jpg";
			case "image/tiff" -> "tiff";
			case "image/bmp" -> "bmp";
			default -> "bin";
		};
	}

	@Transactional
	public void importIAPPRecord(ShallowIAPPRecord record, Blob imageData) {
		SqlRowSet existingCountRowSet = template.queryForRowSet("SELECT count(original_iapp_id) from invasivesbc.iapp_imported_images where original_iapp_id = ?", record.imageID());
		existingCountRowSet.next();
		final boolean preexisting = existingCountRowSet.getLong(1) == 1;
		boolean hasMediaKey = false;

		String newObjectKey = null;
		String detectedMimeType = null;

		boolean uploadCompleted = false;

		if (preexisting) {
			log.info(String.format("Image with IAPP id %s already exists", record.imageID()));
			SqlRowSet existingMediaKeyRowSet = template.queryForRowSet("SELECT media_key from invasivesbc.iapp_imported_images where original_iapp_id = ?", record.imageID());
			if (!existingMediaKeyRowSet.next()) {
				log.error("Expected to find a matching row for query. Skipping this record.");
				return;
			}
			hasMediaKey = existingMediaKeyRowSet.getString(1) != null && !existingMediaKeyRowSet.getString(1).isEmpty();
		}

		if (!preexisting || (preexisting && !hasMediaKey)) {
			ByteArrayOutputStream rawImageData = new ByteArrayOutputStream();

			try (InputStream fromIAPPStream = imageData.getBinaryStream()) {
				StreamUtils.copy(fromIAPPStream, rawImageData);
				detectedMimeType = tikaInstance.detect(rawImageData.toByteArray());
				log.info(String.format("Detected MIME type %s for image %s", detectedMimeType, record.imageID()));
				seenMimeTypes.add(detectedMimeType);

				newObjectKey = "IAPP-%d-%s.%s".formatted(record.imageID(), UUID.randomUUID().toString(), getExtension(detectedMimeType));

				try {
					log.info("Assigning new key %s for upload".formatted(newObjectKey));
					objectStoreInterface.putKey(newObjectKey, new ByteArrayInputStream(rawImageData.toByteArray()), rawImageData.size());
					uploadCompleted = true;
				} catch (ObjectStoreInterface.ObjectStoreException e) {
					log.error("Unexpected error in object store upload ", e);
				}

			} catch (IOException | SQLException e) {
				log.error("Error copying stream data from IAPP", e);
			}
		}

		if (!preexisting) {
			log.info("creating new record in import table");

			template.update("INSERT INTO invasivesbc.iapp_imported_images" +
					"(original_iapp_id," +
					" perspective_code," +
					" sample_point_id," +
					" site_id," +
					" treatment_id," +
					" image_date," +
					" reference_no," +
					" comments," +
					" detected_mime_type," +
					" revision_count_at_import_time," +
					" media_key)" +
					" values (?,?,?,?,?,?,?,?,?,?,?)",
				record.imageID(),
				record.perspectiveCode(),
				record.samplePointID(),
				record.siteID(),
				record.treatmentID(),
				record.imageDate(),
				record.referenceNo(),
				record.comments(),
				detectedMimeType,
				record.revisionCount(),
				null
			);
		}

		if (uploadCompleted) {
			log.info("updating record to reflect new upload key");
			template.update("UPDATE invasivesbc.iapp_imported_images set media_key = ? where original_iapp_id = ?",
				newObjectKey, record.imageID());
		}

	}

}
