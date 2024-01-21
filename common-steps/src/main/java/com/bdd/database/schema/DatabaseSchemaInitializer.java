package com.bdd.database.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSchemaInitializer {

	@Getter
	private final Collection<DatabaseSchema> databaseSchemas;
	@Getter
	private final Map<String, DatabaseSchema> tableNameToSchema = new HashMap<>();

	@PostConstruct
	public void postInit() {
		initSchemas();
	}

	public Optional<DatabaseSchema> getSchemaBy(String tableName) {
		return Optional.ofNullable(tableNameToSchema.get(tableName))
				.or(() -> {
					log.error("Could not find schema of table: {}", tableName);
					return Optional.empty();
				});
	}

	private void initSchemas() {
		databaseSchemas.forEach(this::initSchema);
	}

	private void initSchema(DatabaseSchema schema) {
		schema.getTablesStructures().forEach(table -> {
			var tableName = table.getTableName();
			var schemaName = schema.getSchemaName();
			Optional.ofNullable(tableNameToSchema.get(tableName))
					.filter(existed -> existed.getSchemaName().equals(schemaName))
					.ifPresentOrElse(
							existed -> log.error("A table named [{}] already exists in schema [{}]",
									tableName, schemaName),
							() -> tableNameToSchema.put(tableName, schema)
					);
		});
	}
}
