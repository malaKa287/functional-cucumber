package com.bdd.steps;

import com.bdd.wildcard.WildcardService;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonWildcardSteps {

	private final WildcardService wildcardService;

	@After
	public void cleanup() {
		wildcardService.clear();
	}

	@Given("{word} has value {word}")
	public void map_a_value_to_the_wildcard(String wildcard, String value) {
		wildcardService.addOrUpdate(wildcard, value);
	}
}
