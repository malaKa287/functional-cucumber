package com.test.steps;

import com.test.testcontainers.TestContainersProvider;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

public class DatabaseSteps {

	@BeforeAll
	public static void init() {
		TestContainersProvider.getPostgreSQLContainer().start();
	}

	@AfterAll
	public static void cleanup() {
		TestContainersProvider.getPostgreSQLContainer().stop();
	}
}
