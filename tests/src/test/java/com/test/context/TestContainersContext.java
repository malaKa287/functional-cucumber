package com.test.context;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestContainersContext {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgreSQLContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
				.withUsername("admin")
				.withPassword("admin")
				.withDatabaseName("TEST_DB")
				.withCreateContainerCmdModifier(cmd -> cmd.withName("postgres"))
				.withInitScript("db/postgres/init_postgres.sql");
	}
}
