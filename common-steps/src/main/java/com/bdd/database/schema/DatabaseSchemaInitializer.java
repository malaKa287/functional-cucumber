package com.bdd.database.schema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bdd.database.schema.table.TableIdentifier;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class DatabaseSchemaInitializer {

	private final Collection<DatabaseSchema> databaseSchemas;
	private final Map<TableIdentifier, DatabaseSchema> tableIdToSchema = new HashMap<>();

	@PostConstruct
	public void postInit() {
		initSchemas();
	}

	public String findSchemaName(String tableName) {
		var tableIds = tableIdToSchema.keySet().stream()
				.filter(tableId -> tableId.tableName().equals(tableName))
				.toList();

		if (tableIds.size() > 1) {
			throw new IllegalArgumentException("Table: [%s] is present in multiple schemas: [%s]. Please specify a schema name."
					.formatted(tableName, tableIds.stream().map(TableIdentifier::schemaName).toList()));
		} else if (tableIds.size() == 1) {
			return tableIds.getFirst().schemaName();
		} else {
			throw new IllegalArgumentException("Can't find schema name for the table: [%s].".formatted(tableName));
		}
	}

	public Optional<DatabaseSchema> getSchemaBy(TableIdentifier tableId) {
		return Optional.ofNullable(tableIdToSchema.get(tableId))
				.or(() -> {
					log.error("Table: [{}] not found.", tableId);
					return Optional.empty();
				});
	}

	private void initSchemas() {
		databaseSchemas.forEach(this::initSchema);
	}

	private void initSchema(DatabaseSchema schema) {
		schema.getTablesStructures().forEach(table -> Optional.ofNullable(tableIdToSchema.get(table.getTableIdentifier()))
				.ifPresentOrElse(
						existed -> log.error("A table named [{}] already exists in schema [{}]",
								table.getTableIdentifier().tableName(), table.getTableIdentifier().schemaName()),
						() -> tableIdToSchema.put(table.getTableIdentifier(), schema)
				));
	}
}
