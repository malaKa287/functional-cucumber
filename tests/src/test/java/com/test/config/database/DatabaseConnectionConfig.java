package com.test.config.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bdd.database.connection.DatabaseConnectionData;
import com.bdd.database.connection.DriverType;
import com.test.testcontainers.ContainerProvider;

@Configuration
public class DatabaseConnectionConfig {

	@Bean
	public DatabaseConnectionData postgresConnectionData(DatabaseSchemaConfig schema) {
		var container = ContainerProvider.getPostgreSQLContainer();
		return new DatabaseConnectionData(container.getUsername(), container.getPassword(), container.getJdbcUrl(), DriverType.POSTGRES,
				schema.getSchemaName());
	}
}
