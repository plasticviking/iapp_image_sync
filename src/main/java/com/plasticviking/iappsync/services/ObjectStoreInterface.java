package com.plasticviking.iappsync.services;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.plasticviking.iappsync.configuration.ObjectStoreConfigurationProperties;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ObjectStoreInterface {

	private Logger log = LoggerFactory.getLogger(ObjectStoreInterface.class.getCanonicalName());

	private final AmazonS3 s3;

	private final String BUCKET_NAME;

	public ObjectStoreInterface(AmazonS3 s3, ObjectStoreConfigurationProperties configuration) {
		this.s3 = s3;
		this.BUCKET_NAME = configuration.bucketName();
	}

	public void checkConnectivity() {
		s3.listBuckets();
	}

	public boolean keyExists(String key) {
		return s3.doesObjectExist(BUCKET_NAME, key);
	}

	public void putKey(String key, InputStream data, long size) throws ObjectStoreException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(size);
		try {
			s3.putObject(BUCKET_NAME, key, data, metadata);
		} catch (SdkClientException e) {
			log.error("Unexpected error while uploading file", e);
			throw new ObjectStoreException(e);
		}
		log.info(String.format("Uploaded key %s with size %d", key, size));
	}

	public static class ObjectStoreException extends Exception {
		ObjectStoreException(Exception e) {
			super(e);
		}
	}
}
