package com.bdd.database.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.stereotype.Service;

import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.DatabaseSchemaInitializer;
import com.bdd.database.schema.table.DefaultTableStructure;
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

	public DatabaseSchema getSchemaBy(String tableName) {
		return databaseSchemaInitializer.getSchemaBy(tableName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Schema of table [%s] was not found.", tableName)));
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

	public void deleteAll(TableIdentifier tableIdentifier, IDatabaseConnection connection) {
		var tableStructure = getTableStructure(tableIdentifier.tableName());
		var dataSet = DefaultTableStructure.fromDataTable(tableStructure, DataTable.emptyDataTable());
		execute(DatabaseOperation.DELETE_ALL, connection, dataSet);
	}

	public void insert(TableIdentifier identifier, IDatabaseConnection connection, DataTable dataTable) {
		var tableStructure = getTableStructure(identifier.tableName());
		var dataSet = DefaultTableStructure.fromDataTable(tableStructure, dataTable);
		execute(DatabaseOperation.INSERT, connection, dataSet);
	}

	public ITable findAll(TableIdentifier tableIdentifier, IDatabaseConnection connection) {
		try {
			return connection.createTable(tableIdentifier.tableName());
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Can't fetch data from the: %s" + tableIdentifier.toString(), e);
		}
	}

	public void execute(DatabaseOperation operation, IDatabaseConnection connection, IDataSet dataSet) {
		operationsExecutor.execute(operation, connection, dataSet);
	}

	private List<Map<String, String>> asRowsMaps(ITable table) {
		return IntStream.range(0, table.getRowCount())
				.collect(ArrayList::new,
						(rows, rowIdx) -> accumulateRows(rows, table, rowIdx),
						ArrayList::addAll);
	}

	private void accumulateRows(List<Map<String, String>> rows, ITable table, int rowIdx) {
		Map<String, String> rowsMap = new HashMap<>();
		Arrays.stream(getColumns(table)).forEach(column -> {
					try {
						var rowValue = Optional.ofNullable(table.getValue(rowIdx, column.getColumnName()))
								.map(Object::toString)
								.orElse(null);

						rowsMap.put(column.getColumnName().toUpperCase(), rowValue);
						rows.add(rowsMap);
					}
					catch (DataSetException e) {
						throw new IllegalArgumentException(String.format("Can't find a value for row: [%s] column: [%s]", rowIdx,
								column.getColumnName()), e);
					}
				}
		);
	}

	private Column[] getColumns(ITable table) {
		try {
			return table.getTableMetaData().getColumns();
		}
		catch (DataSetException e) {
			throw new IllegalArgumentException("Can't get columns for the: " + table.getTableMetaData().getTableName(), e);
		}
	}

	private DatabaseSchema getDatabaseSchema(String tableName) {
		return databaseSchemaInitializer.getSchemaBy(tableName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Can't find schema for the [%s] table.", tableName)));
	}

	private TableStructure getTableStructure(String tableName) {
		return getDatabaseSchema(tableName).getTableStructure(tableName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Can't find [%s] table.", tableName)));
	}
}
