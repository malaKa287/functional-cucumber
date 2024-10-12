package com.bdd.steps;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;

public abstract class CommonSteps {

	public static final Integer TIMEOUT_SEC = 5;

	public void verifyAssertion(ThrowingRunnable assertion) {
		Awaitility.await().atMost(TIMEOUT_SEC, TimeUnit.SECONDS).untilAsserted(assertion);
	}
}
