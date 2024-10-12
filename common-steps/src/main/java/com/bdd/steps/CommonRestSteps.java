package com.bdd.steps;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.springframework.http.HttpMethod;

import com.bdd.rest.RestService;
import com.bdd.rest.request.JsonRequestPayload;
import com.bdd.rest.request.RequestParams;
import com.bdd.wildcard.WildcardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;

@RequiredArgsConstructor
public class CommonRestSteps extends CommonSteps {

	public static final Set<HttpMethod> BODYLESS_METHODS = Set.of(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.HEAD);

	private final WildcardService wildcardService;
	private final RestService restService;
	private final ObjectMapper objectMapper;

	@After
	public void cleanup() {
		restService.clearResponses();
	}

	@When("I send {httpMethod} request to {word}")
	public void send(HttpMethod httpMethod, String uriStr) {
		var uri = wildcardService.replaceWildcards(uriStr);
		var requestParams = RequestParams.from(uri);
		restService.send(httpMethod, requestParams);
	}

	@When("I send {httpMethod} request to {word} with the following params")
	public void send(HttpMethod httpMethod, String uriStr, JsonNode jsonParameters) {
		var uri = wildcardService.replaceWildcards(uriStr);
		var params = wildcardService.replaceWildcards(jsonParameters);
		var requestPayload = new JsonRequestPayload(params);
		validate(httpMethod, requestPayload);

		var requestParams = RequestParams.from(uri, requestPayload);
		restService.send(httpMethod, requestParams);
	}

	@Then("the following json response is received")
	public void verifyResponse(JsonNode expectedResponse) {
		verifyAssertion(() -> {
			var lastResponse = restService.getLastResponse();
			Assertions.assertThat(lastResponse)
					.withFailMessage("There are no http responses.\nExpected: \n%s".formatted(expectedResponse))
					.isNotNull();
			Assertions.assertThat(toJsonNode(lastResponse.getBody())).isEqualTo(expectedResponse);
		});
	}

	@Then("the following string response is received")
	public void verifyResponse(String expectedResponse) {
		verifyAssertion(() -> {
			var lastResponse = restService.getLastResponse();
			Assertions.assertThat(lastResponse)
					.withFailMessage("There are no http responses.\nExpected:\n%s".formatted(expectedResponse))
					.isNotNull();
			Assertions.assertThat(lastResponse.getBody()).isEqualTo(expectedResponse.trim());
		});
	}

	@Then("the following json response is received ignore fields: [{listOfStrings}]")
	public void verifyResponseIgnoreFields(List<String> ignoredFields, JsonNode expectedResponse) {
		verifyAssertion(() -> {
			var lastResponse = restService.getLastResponse();
			Assertions.assertThat(lastResponse)
					.withFailMessage("There are no http responses.\nExpected:\n%s \nWith ignored fields: %s"
							.formatted(expectedResponse, ignoredFields))
					.isNotNull();
			assertEqualsIgnoreFields(toJsonNode(lastResponse.getBody()), expectedResponse, ignoredFields);
		});
	}

	@Then("the following json response is received ignore fields: [{listOfStrings}] and contains")
	public void verifyResponseContainsIgnoreFields(List<String> ignoredFields, JsonNode expectedResponse) {
		verifyAssertion(() -> {
			var lastResponse = restService.getLastResponse();
			Assertions.assertThat(lastResponse)
					.withFailMessage("There are no http responses.\nExpected:\n%s \nWith ignored fields: %s"
							.formatted(expectedResponse, ignoredFields))
					.isNotNull();
			assertContainsIgnoreFields(toJsonNode(lastResponse.getBody()), expectedResponse, ignoredFields);
		});
	}

	private void validate(HttpMethod method, JsonRequestPayload payload) {
		if (payload.getBody().isPresent() && BODYLESS_METHODS.contains(method)) {
			throw new IllegalArgumentException("Request body is not supported for [%s] method.".formatted(method));
		}
	}

	private JsonNode toJsonNode(String value) {
		try {
			return objectMapper.readValue(value, JsonNode.class);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting string to json. " + value, e);
		}
	}

	private void assertEqualsIgnoreFields(JsonNode actual, JsonNode expected, List<String> ignoredFields) {
		JsonAssertions.assertThatJson(actual)
				.whenIgnoringPaths(ignoredFields.toArray(new String[0]))
				.isEqualTo(expected);
	}

	private void assertContainsIgnoreFields(JsonNode actual, JsonNode expected, List<String> ignoredFields) {
		JsonAssertions.assertThatJson(actual)
				.whenIgnoringPaths(ignoredFields.toArray(new String[0]))
				.isArray()
				.contains(expected);
	}
}
