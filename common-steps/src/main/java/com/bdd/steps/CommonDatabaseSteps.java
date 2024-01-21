package com.bdd.steps;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.dbunit.Assertion;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.SortedTable;

import com.bdd.database.connection.DatabaseConnectionProvider;
import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.table.CleanupStrategyType;
import com.bdd.database.schema.table.DefaultTableStructure;
import com.bdd.database.schema.table.TableIdentifier;
import com.bdd.database.schema.table.TableStructure;
import com.bdd.database.service.DbService;
import com.bdd.database.validator.DbValidator;
import com.bdd.wildcard.WildcardService;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class CommonDatabaseSteps {

	public static final Duration ASSERT_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
	public static final Duration ASSERT_POLL_INTERVAL = Duration.of(100, ChronoUnit.MILLIS);

	private final DatabaseConnectionProvider databaseConnectionProvider;
	private final DbService dbService;
	private final WildcardService wildcardService;
	private final DbValidator dbValidator;

	private List<TableIdentifier> tablesToCleanup;

	@PostConstruct
	public void init() {
		tablesToCleanup = dbService.getSchemas().stream()
				.map(DatabaseSchema::getTablesStructures)
				.flatMap(Collection::stream)
				.filter(dbTableStructure -> dbTableStructure.getCleanupStrategyType() == CleanupStrategyType.SCENARIO)
				.map(dbService::deletionCascade)
				.flatMap(Collection::stream)
				.toList();
	}

	@Before
	public void before() {
		var databaseConnection = databaseConnectionProvider.getConnection();
		tablesToCleanup.forEach(tableIdentifier -> dbService.deleteAll(tableIdentifier, databaseConnection));
	}

	@Given("table {word} contains")
	public void setting_up_content_of_table(String tableName, DataTable dataTable) {
		var dbSchema = dbService.getSchemaBy(tableName);
		setting_up_content_of_table(tableName, dbSchema.getSchemaName(), dataTable);
	}

	@Given("table {word} of schema {word} contains")
	public void setting_up_content_of_table(String tableName, String schemaName, DataTable dataTable) {
		var schema = dbService.getSchemaBy(tableName);
		var tableStructure = getTableStructure(tableName, schema);

		var dbColumnNames = getDbColumnNames(tableStructure.getColumns());
		var expectedColumnNames = new HashSet<>(dataTable.row(0));

		dbValidator.validateColumnNames(tableName, expectedColumnNames, dbColumnNames);

		var resolvedDataTable = wildcardService.replaceWildcards(dataTable);
		var databaseConnection = databaseConnectionProvider.getConnection();
		var tableIdentifier = tableIdentifier(tableName, schemaName);

		dbService.insert(tableIdentifier, databaseConnection, resolvedDataTable);
	}

	@Then("verify table {word} contains")
	public void verify_db_contains_data_table(String tableName, DataTable dataTable) {
		var schema = dbService.getSchemaBy(tableName);
		var tableStructure = getTableStructure(tableName, schema);
		var dbColumnNames = getDbColumnNames(tableStructure.getColumns());
		var expectedColumnNames = new HashSet<>(dataTable.row(0));

		dbValidator.validateColumnNames(tableName, expectedColumnNames, dbColumnNames);

		var tableIdentifier = tableIdentifier(tableName, schema.getSchemaName());
		var expectedColumns = getExpectedColumns(tableStructure, expectedColumnNames);
		var ignoredColumnNames = getIgnoredColumnNames(tableStructure, expectedColumnNames);
		var resolvedDataTable = wildcardService.replaceWildcards(dataTable);

		Awaitility.await().atMost(ASSERT_TIMEOUT)
				.pollInterval(ASSERT_POLL_INTERVAL)
				.untilAsserted(() -> {
					var actualTable = dbService.fetchData(tableIdentifier, databaseConnectionProvider.getConnection());
					var actualSorted = new SortedTable(actualTable, expectedColumns, true);

					var expectedTable = DefaultTableStructure.fromDataTable(tableStructure, resolvedDataTable)
							.getTable(tableName);
					var expectedSorted = new SortedTable(expectedTable, expectedColumns, true);

					Assertion.assertEqualsIgnoreCols(expectedSorted, actualSorted, ignoredColumnNames);
				});
	}

	private TableIdentifier tableIdentifier(String tableName, String schemaName) {
		return new TableIdentifier(schemaName, tableName);
	}

	private TableStructure getTableStructure(String tableName, DatabaseSchema schema) {
		return schema.getTableStructure(tableName)
				.orElseThrow(() -> new IllegalArgumentException(String.format("Can't find DB table structure for: [%s.%s]",
						schema.getSchemaName(), tableName)));
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
}
