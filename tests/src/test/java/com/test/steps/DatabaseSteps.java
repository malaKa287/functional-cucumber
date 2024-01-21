package com.test.steps;

import com.test.testcontainers.ContainerProvider;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

public class DatabaseSteps {

	@BeforeAll
	public static void init() {
		ContainerProvider.getPostgreSQLContainer().start();
	}

	@AfterAll
	public static void cleanup() {
		ContainerProvider.getPostgreSQLContainer().stop();
	}
}
