package com.bdd.database.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.stereotype.Service;

import com.bdd.database.mapper.DatabaseTableConverter;
import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.DatabaseSchemaInitializer;
import com.bdd.database.schema.table.TableIdentifier;
import com.bdd.database.schema.table.TableStructure;

import io.cucumber.datatable.DataTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {

	private final DatabaseSchemaInitializer databaseSchemaInitializer;
	private final DatabaseOperationsExecutor operationsExecutor;

	public Collection<DatabaseSchema> getSchemas() {
		return databaseSchemaInitializer.getDatabaseSchemas();
	}

	public String findSchemaName(String tableName) {
		return databaseSchemaInitializer.findSchemaName(tableName);
	}

	public DatabaseSchema getSchemaBy(TableIdentifier tableIdentifier) {
		return databaseSchemaInitializer.getSchemaBy(tableIdentifier)
				.orElseThrow(() -> new IllegalArgumentException("Schema was not found for [%s].".formatted(tableIdentifier)));
	}

	public List<TableIdentifier> deletionCascade(TableStructure tableStructure) {
		List<TableIdentifier> result = new ArrayList<>();
		result.add(tableStructure.getTableIdentifier());
		for (var fk : tableStructure.getForeignKeys()) {
			result.addAll(deletionCascade(fk.dependantColumn().structure()));
		}
		Collections.reverse(result);
		return result;
	}

	public void deleteAll(TableIdentifier tableId, IDatabaseConnection connection) {
		var tableStructure = getTableStructure(tableId);
		var dataSet = DatabaseTableConverter.fromDataTable(tableStructure, DataTable.emptyDataTable());
		execute(DatabaseOperation.DELETE_ALL, connection, dataSet);
	}

	public void insert(TableIdentifier tableId, IDatabaseConnection connection, DataTable dataTable) {
		var tableStructure = getTableStructure(tableId);
		var dataSet = DatabaseTableConverter.fromDataTable(tableStructure, dataTable);
		execute(DatabaseOperation.INSERT, connection, dataSet);
	}

	public ITable findAll(TableIdentifier tableIdentifier, IDatabaseConnection connection) {
		try {
			return connection.createTable(tableIdentifier.tableName());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Can't fetch data from the: [%s]" + tableIdentifier.toString(), e);
		}
	}

	public void execute(DatabaseOperation operation, IDatabaseConnection connection, IDataSet dataSet) {
		operationsExecutor.execute(operation, connection, dataSet);
	}

	private DatabaseSchema getDatabaseSchema(TableIdentifier tableId) {
		return databaseSchemaInitializer.getSchemaBy(tableId)
				.orElseThrow(() -> new IllegalArgumentException("Can't find schema for the [%s] table.".formatted(tableId)));
	}

	private TableStructure getTableStructure(TableIdentifier tableId) {
		return getDatabaseSchema(tableId).getTableStructure(tableId)
				.orElseThrow(() -> new IllegalArgumentException("Can't find [%s] table.".formatted(tableId)));
	}
}
