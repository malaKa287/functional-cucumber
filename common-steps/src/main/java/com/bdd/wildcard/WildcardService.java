package com.bdd.wildcard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.datatable.DataTable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Service
@RequiredArgsConstructor
public class WildcardService {

	public static final Pattern WILDCARD_PATTERN = Pattern.compile("\\$\\{(.*?)}");

	private final ObjectMapper objectMapper;

	private final Map<String, String> wildcards = new HashMap<>();

	public void clear() {
		wildcards.clear();
	}

	public void addOrUpdate(String key, String value) {
		wildcards.put(key, value);
	}

	public String getFor(String key) {
		return wildcards.get(key);
	}

	public String replaceWildcards(String input) {
		return applyWildcards(input);
	}

	public JsonNode replaceWildcards(JsonNode input) {
		try {
			var jsonString = objectMapper.writeValueAsString(input);
			var resolvedString = applyWildcards(jsonString);
			return objectMapper.readTree(resolvedString);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error replacing wildcards in JSON. Please check the structure.\n%s"
					.formatted(input), e);
		}
	}

	public DataTable replaceWildcards(DataTable dataTable) {
		var resolvedDataTable = applyWildcards(dataTable);
		return DataTable.create(resolvedDataTable);
	}

	private List<List<String>> applyWildcards(DataTable dataTable) {
		return dataTable.asLists().stream()
				.map(list -> list.stream()
						.map(this::applyWildcards)
						.toList())
				.toList();
	}

	private String applyWildcards(String data) {
		if (data == null || data.isEmpty()) {
			return data;
		}
		var rex = WILDCARD_PATTERN.matcher(data);
		var result = data;
		while (rex.find()) {
			var wrappedWildcardName = rex.group(0);
			var wildcardName = rex.group(1);
			var definedWildcard = wildcards.get(wildcardName);
			if (definedWildcard == null) {
				throw new IllegalArgumentException("There is no defined wildcard: " + wildcardName);
			}
			result = result.replace(wrappedWildcardName, definedWildcard);
		}
		return result;
	}
}
