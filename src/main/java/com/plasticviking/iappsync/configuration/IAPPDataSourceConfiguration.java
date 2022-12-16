package com.plasticviking.iappsync.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class IAPPDataSourceConfiguration {

	@Bean
	@ConfigurationProperties("spring.datasource.iapp")
	public DataSourceProperties IAPPDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource IAPPDataSource() {
		return IAPPDataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}

	@Bean
	public JdbcTemplate IAPPJdbcTemplate(@Qualifier("IAPPDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
