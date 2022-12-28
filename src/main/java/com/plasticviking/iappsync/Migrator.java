package com.plasticviking.iappsync;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.plasticviking.iappsync.configuration.ObjectStoreConfigurationProperties;
import com.plasticviking.iappsync.services.IAPPInterface;
import com.plasticviking.iappsync.services.InvasivesInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class Migrator implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(Migrator.class.getCanonicalName());

	@Autowired
	IAPPInterface iappInterface;

	@Autowired
	InvasivesInterface invasivesInterface;

	@Autowired
	AmazonS3 objectStoreClient;

	@Autowired
	ObjectStoreConfigurationProperties objectStoreConfigurationProperties;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Starting up");

		AtomicLong processed = new AtomicLong(0);

//		iappInterface.processIAPPImages((record, imageData) -> {
//			invasivesInterface.importIAPPRecord(record, imageData);
//			processed.incrementAndGet();
//		});

		ObjectListing objects = objectStoreClient.listObjects(objectStoreConfigurationProperties.bucketName());
		objects.getObjectSummaries().stream().forEach(summary -> {
			log.info("Object: " + summary.getKey());
		});

		log.info("Run complete, processed " + processed.get() + " records");
	}
}
