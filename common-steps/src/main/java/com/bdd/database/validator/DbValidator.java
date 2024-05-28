package com.bdd.database.validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class DbValidator {

	public void validateColumnNames(String tableName, Set<String> expectedColumnNames, Set<String> actualColumnNames) {
		var notMappedColumns = expectedColumnNames.stream()
				.filter(expectedName -> !actualColumnNames.contains(expectedName))
				.collect(Collectors.toSet());

		if (!notMappedColumns.isEmpty()) {
			throw new IllegalArgumentException("Unable to define appropriate columns for table: [%s]. Unmapped columns: %s"
					.formatted(tableName, notMappedColumns));
		}
	}
}
