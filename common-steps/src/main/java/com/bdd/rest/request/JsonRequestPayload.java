package com.bdd.rest.request;

import java.util.Optional;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.NonNull;

public record JsonRequestPayload(JsonNode jsonRequest) {

	private static final Set<String> ALLOWED_FIELDS = Set.of("requestMetadata", "body");

	public JsonRequestPayload(@NonNull JsonNode jsonRequest) {
		validate(jsonRequest);
		this.jsonRequest = jsonRequest;
	}

	public Optional<String> getBody() {
		return extractTextField("body");
	}

	public Optional<String> getUsername() {
		return extractTextField("username");
	}

	public Optional<String> getPassword() {
		return extractTextField("password");
	}

	public MultiValueMap<String, String> getHeaders() {
		return extractHeaders();
	}

	private void validate(JsonNode jsonRequest) {
		var fieldNames = jsonRequest.fieldNames();
		while (fieldNames.hasNext()) {
			var fieldName = fieldNames.next();
			if (!ALLOWED_FIELDS.contains(fieldName)) {
				throw new IllegalArgumentException("Not supported field [%s] in json. Expected: %s"
						.formatted(fieldName, ALLOWED_FIELDS));
			}
		}
	}

	private Optional<String> extractTextField(String fieldName) {
		return Optional.ofNullable(jsonRequest.get(fieldName))
				.map(JsonNode::toString);
	}

	private MultiValueMap<String, String> extractHeaders() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		var requestMetadata = jsonRequest.get("requestMetadata");
		if (requestMetadata != null) {
			var headersNode = requestMetadata.get("headers");
			if (headersNode != null) {
				headersNode.fields().forEachRemaining(header ->
						headers.add(header.getKey(), header.getValue().asText())
				);
			}
		}
		return headers;
	}
}
