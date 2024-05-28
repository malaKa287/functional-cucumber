package com.test.context.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.datatype.DataType;
import org.springframework.context.annotation.Configuration;

import com.bdd.database.connection.DatabaseConnectionData;
import com.bdd.database.connection.DriverType;
import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.table.CleanupStrategyType;
import com.bdd.database.schema.table.ColumnIdentifier;
import com.bdd.database.schema.table.ConstraintActionType;
import com.bdd.database.schema.table.DefaultTableStructure;
import com.bdd.database.schema.table.TableIdentifier;
import com.bdd.database.schema.table.TableStructure;
import com.test.testcontainers.TestContainersProvider;

import lombok.Getter;

@Configuration
public class PostgresSchemaConfig implements DatabaseSchema {

	@Getter
	private final String schemaName;
	@Getter
	private final Collection<TableStructure> tablesStructures;
	private final Map<TableIdentifier, TableStructure> tableIdToStructure;

	public PostgresSchemaConfig() {
		this.schemaName = "TEST_SCHEMA";
		this.tablesStructures = createTablesStructures();
		this.tableIdToStructure = tablesStructures.stream()
				.collect(Collectors.toMap(TableStructure::getTableIdentifier, Function.identity()));
	}

	@Override
	public Optional<TableStructure> getTableStructure(TableIdentifier tableName) {
		return Optional.ofNullable(tableIdToStructure.get(tableName));
	}

	@Override
	public DatabaseConnectionData getConnectionData() {
		var container = TestContainersProvider.getPostgreSQLContainer();
		return new DatabaseConnectionData(container.getUsername(), container.getPassword(), container.getJdbcUrl(),
				DriverType.POSTGRES, this.schemaName);
	}

	private Collection<TableStructure> createTablesStructures() {
		var testUsers = usersStructure();
		var testContacts = contactsStructure();

		testUsers.registerForeignKeyConstraint("ID", new ColumnIdentifier(testContacts, "USER_ID"));

		return Stream.of(testUsers, testContacts)
				.toList();
	}

	private TableStructure usersStructure() {
		return new DefaultTableStructure(
				schemaName,
				"TEST_USERS",
				CleanupStrategyType.SCENARIO,
				ConstraintActionType.SET_NULL,
				new Column("ID", DataType.NUMERIC, Column.NO_NULLS),
				new Column("FIRST_NAME", DataType.VARCHAR, Column.NO_NULLS),
				new Column("LAST_NAME", DataType.VARCHAR)
		);
	}

	private TableStructure contactsStructure() {
		return new DefaultTableStructure(
				schemaName,
				"TEST_CONTACTS",
				new Column("ID", DataType.NUMERIC, Column.NO_NULLS),
				new Column("USER_ID", DataType.VARCHAR),
				new Column("PHONE_NUMBER", DataType.NUMERIC, Column.NO_NULLS),
				new Column("EMAIL", DataType.VARCHAR)
		);
	}

}
