package com.plasticviking.iappsync.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class InvasivesDataSourceConfiguration {

	@Bean
	@ConfigurationProperties("spring.datasource.invasives")
	public DataSourceProperties InvasivesDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource InvasivesDataSource() {
		return InvasivesDataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}

	@Bean
	public JdbcTemplate InvasivesJdbcTemplate(@Qualifier("InvasivesDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
