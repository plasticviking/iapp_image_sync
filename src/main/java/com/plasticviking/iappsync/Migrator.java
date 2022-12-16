package com.plasticviking.iappsync;

import com.plasticviking.iappsync.services.IAPPInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Migrator implements ApplicationRunner {

	private Logger log = LoggerFactory.getLogger(Migrator.class.getCanonicalName());

	@Autowired
	IAPPInterface iappInterface;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("Starting up");

		byte[] data = iappInterface.getImageData(200001);
		byte[] encoded = Base64.getEncoder().encode(data);
		System.out.println(encoded);

		log.info("Run complete");
	}
}
