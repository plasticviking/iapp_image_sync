package com.plasticviking.iappsync;

import com.plasticviking.iappsync.services.IAPPInterface;
import com.plasticviking.iappsync.services.InvasivesInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Migrator implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(Migrator.class.getCanonicalName());

	@Autowired
	IAPPInterface iappInterface;

	@Autowired
	InvasivesInterface invasivesInterface;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Starting up");

		iappInterface.processIAPPImages(record -> invasivesInterface.hasImage(record.imageID()));

		log.info("Run complete");
	}
}
