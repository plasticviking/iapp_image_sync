package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.data.ShallowIAPPRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IAPPInterface {

	private final Logger log = LoggerFactory.getLogger(IAPPInterface.class.getCanonicalName());

	private final JdbcTemplate template;

	public IAPPInterface(@Qualifier("IAPPJdbcTemplate") JdbcTemplate template) {
		this.template = template;
	}

	public byte[] getImageData(long imageId) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final AtomicBoolean success = new AtomicBoolean(false);

		template.query("SELECT IMAGE FROM IAPP_IMAGE WHERE IMAGE_ID = ?", rs -> {
				try {
					StreamUtils.copy(rs.getBlob(1).getBinaryStream(), baos);
					success.set(true);
				} catch (IOException e) {
					log.error("Unhandled exception while copying image data", e);
				}
			},
			imageId
		);

		return baos.toByteArray();
	}

	public void processIAPPImages(IAPPImageConsumer consumer) {
		final int MAX_PER_RUN = 10;
		AtomicInteger processed = new AtomicInteger(0);

		template.query("SELECT IMAGE_ID, SAMPLE_POINT_ID, SITE_ID, TREATMENT_ID, IMAGE, PERSPECTIVE_CODE,REVISION_COUNT, COMMENTS, REFERENCE_NO, IMAGE_DATE, INVASIVE_PLANT_AGENCY_CODE FROM IAPP_IMAGE ORDER BY IMAGE_ID ASC",
			rs -> {
				while (rs.next() && processed.get() < MAX_PER_RUN) {
					ShallowIAPPRecord row = new ShallowIAPPRecord(
						rs.getLong(1),
						rs.getLong(2),
						rs.getLong(3),
						rs.getLong(4),
						rs.getString(6),
						rs.getLong(7),
						rs.getString(8),
						rs.getString(9),
						rs.getDate(10)
					);

					consumer.consume(row, rs.getBlob(5));
					processed.incrementAndGet();
				}
			});

	}

}
