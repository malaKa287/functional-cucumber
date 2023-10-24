package com.bdd.steps;

import org.assertj.core.api.Assertions;

import com.bdd.service.WildcardService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonWildcardSteps {

	private final WildcardService wildcardService;

	@Given("{word} has value {word}")
	public void map_a_value_to_the_wildcard(String wildcard, String value) {
		wildcardService.addOrUpdate(wildcard, value);
	}

	@Then("{word} should have value {word}")
	public void verify_wildcard_value(String wildcard, String value) {
		var wildcardValue = wildcardService.getFor(wildcard);
		Assertions.assertThat(value).isEqualTo(wildcardValue);
	}
}
