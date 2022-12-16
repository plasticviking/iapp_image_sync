package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.Migrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
				StreamUtils.copy(rs.getBinaryStream(0), baos);
				success.set(true);
			} catch (IOException e) {
				log.error("Unhandled exception while copying image data", e);
			}
		}, imageId);

		return baos.toByteArray();
	}
}
