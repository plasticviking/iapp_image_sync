package com.plasticviking.iappsync.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.AwsHostNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ObjectStore {

	@Autowired
	private ObjectStoreConfigurationProperties objectStoreConfiguration;

	@Bean
	AmazonS3 s3Client() {
		return AmazonS3ClientBuilder.standard()
			.withCredentials(
				new AWSStaticCredentialsProvider(
					new BasicAWSCredentials(objectStoreConfiguration.accessKeyId(), objectStoreConfiguration.secretAccessKey())
				)
			)
			.withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(
					objectStoreConfiguration.endpoint(),
					AwsHostNameUtils.parseRegion(objectStoreConfiguration.endpoint(), AmazonS3Client.S3_SERVICE_NAME)
				)
			).withPathStyleAccessEnabled(true)
			.build();
	}
}
