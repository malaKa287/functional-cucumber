package com.bdd.wildcard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import lombok.Getter;

@Getter
public class WildcardService {

	public static final Pattern WILDCARD_PATTERN = Pattern.compile("\\$\\{(.*?)}");

	private final Map<String, String> wildcards = new HashMap<>();

	@After
	public void cleanup() {
		wildcards.clear();
	}

	public void addOrUpdate(String key, String value) {
		wildcards.put(key, value);
	}

	public String getFor(String key) {
		return wildcards.get(key);
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
