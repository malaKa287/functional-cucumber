package com.test.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersProvider {

	private static PostgreSQLContainer<?> POSTGRES_CONTAINER;

	public static PostgreSQLContainer<?> getPostgreSQLContainer() {
		if (POSTGRES_CONTAINER == null) {
			POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
					.withUsername("admin")
					.withPassword("admin")
					.withDatabaseName("TEST_DB")
					.withCreateContainerCmdModifier(cmd -> cmd.withName("postgres"))
					.withInitScript("db/postgres/init_postgres.sql");
		}
		return POSTGRES_CONTAINER;
	}
}
