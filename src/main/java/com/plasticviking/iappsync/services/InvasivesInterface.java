package com.plasticviking.iappsync.services;

import com.plasticviking.iappsync.data.ShallowIAPPRecord;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

@Component
public class InvasivesInterface {

	private Logger log = LoggerFactory.getLogger(InvasivesInterface.class.getCanonicalName());

	private final JdbcTemplate template;

	public InvasivesInterface(@Qualifier("InvasivesJdbcTemplate") JdbcTemplate template) {
		this.template = template;
	}

	@Transactional
	public void importIAPPRecord(ShallowIAPPRecord record) {
		log.info(String.format("Importing IAPP Image with ID %s", record.imageID()));
	}


}
