package com.plasticviking.iappsync.services;

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
@Transactional
public class InvasivesInterface {

	private Logger log = LoggerFactory.getLogger(InvasivesInterface.class.getCanonicalName());

	private final JdbcTemplate template;

	public InvasivesInterface(@Qualifier("InvasivesJdbcTemplate") JdbcTemplate template) {
		this.template = template;
	}

	public boolean hasImage(long id) {
		return true;
	};


}
