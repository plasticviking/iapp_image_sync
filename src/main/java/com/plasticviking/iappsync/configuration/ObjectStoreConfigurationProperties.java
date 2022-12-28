package com.plasticviking.iappsync.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


@ConfigurationProperties(prefix = "objectstore")
@ConstructorBinding
public record ObjectStoreConfigurationProperties(String endpoint, String accessKeyId, String secretAccessKey, String bucketName) {}
