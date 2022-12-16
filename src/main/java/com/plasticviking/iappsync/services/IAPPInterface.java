package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.data.ShallowIAPPRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class IAPPInterface {

	private Logger log = LoggerFactory.getLogger(IAPPInterface.class.getCanonicalName());

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
		template.query("SELECT IMAGE_ID, SAMPLE_POINT_ID, SITE_ID, TREATMENT_ID FROM IAPP_IMAGE ORDER BY IMAGE_ID ASC",
			rs -> {
				while (rs.next()) {
					ShallowIAPPRecord row = new ShallowIAPPRecord(
						rs.getLong(1),
						Optional.of(rs.getLong(2)),
						Optional.of(rs.getLong(3)),
						Optional.of(rs.getLong(4)));

					consumer.consume(row);
				}
			});


	}

}
