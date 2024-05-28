package com.bdd.steps;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.SortedTable;

import com.bdd.database.connection.DatabaseConnectionProvider;
import com.bdd.database.mapper.DatabaseTableConverter;
import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.table.CleanupStrategyType;
import com.bdd.database.schema.table.TableIdentifier;
import com.bdd.database.schema.table.TableStructure;
import com.bdd.database.service.DatabaseService;
import com.bdd.database.validator.DbValidator;
import com.bdd.wildcard.WildcardService;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class CommonDatabaseSteps {

	private final DatabaseConnectionProvider databaseConnectionProvider;
	private final DatabaseService databaseService;
	private final WildcardService wildcardService;
	private final DbValidator dbValidator;
	private final DatabaseTableConverter tableConverter;

	private List<TableIdentifier> tablesToCleanup;

	@PostConstruct
	public void init() {
		tablesToCleanup = databaseService.getSchemas().stream()
				.map(DatabaseSchema::getTablesStructures)
				.flatMap(Collection::stream)
				.filter(dbTableStructure -> dbTableStructure.getCleanupStrategyType() == CleanupStrategyType.SCENARIO)
				.map(databaseService::deletionCascade)
				.flatMap(Collection::stream)
				.toList();
	}

	@Before
	public void before() {
		tablesToCleanup.forEach(tableIdentifier -> {
			var schema = databaseService.getSchemaBy(tableIdentifier);
			var connection = databaseConnectionProvider.getConnection(schema);
			databaseService.deleteAll(tableIdentifier, connection.getConnection());
		});
	}

	@Given("clear table {word}")
	public void clearTable(String tableName) {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);

		databaseService.deleteAll(tableIdentifier, databaseConnection.getConnection());
	}

	@Given("table {word} contains")
	public void populateTable(String tableName, DataTable dataTable) {
		var schemaName = databaseService.findSchemaName(tableName);
		populateTable(schemaName, tableName, dataTable);
	}

	@Given("table {word} of schema {word} contains")
	public void populateTable(String schemaName, String tableName, DataTable dataTable) {
		var tableId = new TableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableId);
		var tableStructure = getTableStructure(tableId, schema);

		var dbColumnNames = getDbColumnNames(tableStructure.getColumns());
		var expectedColumnNames = new HashSet<>(dataTable.row(0));

		dbValidator.validateColumnNames(tableName, expectedColumnNames, dbColumnNames);

		var resolvedDataTable = wildcardService.replaceWildcards(dataTable);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);
		var tableIdentifier = tableIdentifier(schemaName, tableName);

		databaseService.insert(tableIdentifier, databaseConnection.getConnection(), resolvedDataTable);
	}

	@When("insert into table {word}")
	public void insertToTable(String tableName, DataTable dataTable) {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);
		var resolvedDataTable = wildcardService.replaceWildcards(dataTable);

		databaseService.insert(tableIdentifier, databaseConnection.getConnection(), resolvedDataTable);
	}

	@Then("verify table {word} is empty")
	public void verifyTableIsEmpty(String tableName) throws DatabaseUnitException {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);

		var currentTable = databaseService.findAll(tableIdentifier, databaseConnection.getConnection());
		var expectedTable = new DefaultTable(tableName); // empty table

		Assertion.assertEquals(expectedTable, currentTable);
	}

	@Then("verify table {word} contains")
	public void verifyDbTableContains(String tableName, DataTable dataTable) throws DatabaseUnitException {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var tableStructure = getTableStructure(tableIdentifier, schema);
		var dbColumnNames = getDbColumnNames(tableStructure.getColumns());
		var expectedColumnNames = new HashSet<>(dataTable.row(0));

		dbValidator.validateColumnNames(tableName, expectedColumnNames, dbColumnNames);

		var expectedColumns = getExpectedColumns(tableStructure, expectedColumnNames);
		var ignoredColumnNames = getIgnoredColumnNames(tableStructure, expectedColumnNames);
		var resolvedDataTable = wildcardService.replaceWildcards(dataTable);

		var expectedTable = DatabaseTableConverter.fromDataTable(tableStructure, resolvedDataTable).getTable(tableName);
		var expectedSorted = new SortedTable(expectedTable, expectedColumns, true);

		var databaseConnection = databaseConnectionProvider.getConnection(schema);
		var currentTable = databaseService.findAll(tableIdentifier, databaseConnection.getConnection());
		var currentSorted = new SortedTable(currentTable, expectedColumns, true);

		Assertion.assertEqualsIgnoreCols(expectedSorted, currentSorted, ignoredColumnNames);
	}

	@Then("verify table {word} contains at least")
	public void verifyDbTableContainsAtLeast(String tableName, DataTable dataTable) {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);
		var currentContent = databaseService.findAll(tableIdentifier, databaseConnection.getConnection());

		var expectedColumnNames = new HashSet<>(dataTable.row(0));
		var tableStructure = getTableStructure(tableIdentifier, schema);
		var ignoredColumnNames = getIgnoredColumnNames(tableStructure, expectedColumnNames);

		var currentRows = tableConverter.asRowsMaps(currentContent);
		var expectedRows = wildcardService.replaceWildcards(dataTable).entries();
		var matchedRows = findEqualsRows(currentRows, expectedRows, ignoredColumnNames);

		Assertions.assertThat(matchedRows.size())
				.withFailMessage("Expected [%s] matched rows but was [%s]. Can't find: [%s].",
						expectedRows.size(), matchedRows.size(), retainNotMatched(expectedRows, currentRows))
				.isEqualTo(expectedRows.size());
	}

	@Then("verify table {word} contains ignore columns")
	public void verifyDbTableContainsIgnoreColumns(String tableName, DataTable dataTable) {
		var schemaName = databaseService.findSchemaName(tableName);
		var tableIdentifier = tableIdentifier(schemaName, tableName);
		var schema = databaseService.getSchemaBy(tableIdentifier);
		var databaseConnection = databaseConnectionProvider.getConnection(schema);
		var currentContent = databaseService.findAll(tableIdentifier, databaseConnection.getConnection());

		var currentRows = tableConverter.asRowsMaps(currentContent);
		var expectedRows = wildcardService.replaceWildcards(dataTable);

		var expectedColumnNames = new HashSet<>(dataTable.row(0));
		var tableStructure = getTableStructure(tableIdentifier, schema);
		var ignoredColumnNames = getIgnoredColumnNames(tableStructure, expectedColumnNames);

		expectedRows.entries().forEach(expectedRow ->
				Assertions.assertThat(containsIgnoreColumns(currentRows, expectedRow, ignoredColumnNames))
						.withFailMessage("Can't find row: " + expectedRow)
						.isTrue());
	}

	private TableIdentifier tableIdentifier(String schemaName, String tableName) {
		return new TableIdentifier(schemaName, tableName);
	}

	private TableStructure getTableStructure(TableIdentifier tableIdentifier, DatabaseSchema schema) {
		return schema.getTableStructure(tableIdentifier)
				.orElseThrow(() -> new IllegalArgumentException("Can't find DB table structure for: [%s]".formatted(tableIdentifier)));
	}

	private Set<String> getDbColumnNames(Column[] dbColumns) {
		return Arrays.stream(dbColumns)
				.map(column -> column.getColumnName().toUpperCase())
				.collect(Collectors.toSet());
	}

	private String[] getIgnoredColumnNames(TableStructure tableStructure, HashSet<String> expectedColumnNames) {
		return Arrays.stream(tableStructure.getColumns())
				.map(Column::getColumnName)
				.filter(columnName -> !expectedColumnNames.contains(columnName))
				.toArray(String[]::new);
	}

	private Column[] getExpectedColumns(TableStructure tableStructure, HashSet<String> expectedColumnNames) {
		return Arrays.stream(tableStructure.getColumns())
				.filter(column -> expectedColumnNames.contains(column.getColumnName()))
				.toArray(Column[]::new);
	}

	private List<Map<String, String>> findEqualsRows(List<Map<String, String>> currentTable, List<Map<String, String>> expectedTable,
			String[] ignoredColumns) {
		return expectedTable.stream()
				.filter(expectedRow -> containsIgnoreColumns(currentTable, expectedRow, ignoredColumns))
				.toList();
	}

	private boolean containsIgnoreColumns(List<Map<String, String>> currentTable, Map<String, String> expectedRow,
			String[] ignoredColumns) {
		return currentTable.stream()
				.anyMatch(currentRow -> isEqualsIgnoreColumns(currentRow, expectedRow, ignoredColumns));
	}

	private boolean isEqualsIgnoreColumns(Map<String, String> currentRow, Map<String, String> expectedRow, String[] ignoredColumns) {
		return currentRow.entrySet().stream()
				.allMatch(row -> containsIgnoreKey(row.getKey(), ignoredColumns)
						|| matchRowValues(row.getValue(), expectedRow.get(row.getKey())));
	}

	private boolean matchRowValues(String currentValue, String expectedValue) {
		return Optional.ofNullable(currentValue)
				.map(value -> value.equals(expectedValue))
				.orElseGet(() -> expectedValue == null);
	}

	private boolean containsIgnoreKey(String key, String[] ignoredKeys) {
		return Arrays.asList(ignoredKeys).contains(key);
	}

	private List<Map<String, String>> retainNotMatched(List<Map<String, String>> expected, List<Map<String, String>> current) {
		return expected.stream()
				.filter(row -> !current.contains(row))
				.distinct()
				.toList();
	}
}
