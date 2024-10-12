package com.bdd.transformer;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.DocStringType;
import io.cucumber.java.ParameterType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParametersTransformer {

	private final ObjectMapper objectMapper;

	@ParameterType("(?:[^,]*)(?:,\\s?[^,]*)*")
	public List<String> listOfStrings(String arg) {
		return Arrays.asList(arg.split(",\\s?"));
	}

	@ParameterType("GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS")
	public HttpMethod httpMethod(String method) {
		return HttpMethod.valueOf(method);
	}

	@DocStringType
	public JsonNode jsonNode(String value) {
		try {
			return objectMapper.readTree(value);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting string to json.", e);
		}
	}

	// @DefaultParameterTransformer
	// @DefaultDataTableEntryTransformer
	// @DefaultDataTableCellTransformer
	// public Object defaultTransformer(Object fromValue, Type toValueType) {
	// 	return objectMapper.convertValue(fromValue, objectMapper.constructType(toValueType));
	// }
}
