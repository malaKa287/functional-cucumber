package com.test.config.database;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.springframework.context.annotation.Configuration;

import com.bdd.database.schema.DatabaseSchema;
import com.bdd.database.schema.table.CleanupStrategyType;
import com.bdd.database.schema.table.ColumnIdentifier;
import com.bdd.database.schema.table.ConstraintActionType;
import com.bdd.database.schema.table.DefaultTableStructure;
import com.bdd.database.schema.table.TableStructure;

import lombok.Getter;

@Configuration
public class DatabaseSchemaConfig implements DatabaseSchema {

	@Getter
	private final String schemaName;
	@Getter
	private final Collection<TableStructure> tablesStructures;
	private final Map<String, TableStructure> tableNameToStructure;

	public DatabaseSchemaConfig() {
		this.schemaName = "TEST_SCHEMA";
		this.tablesStructures = createTablesStructures();
		this.tableNameToStructure = tablesStructures.stream()
				.collect(Collectors.toMap(ITableMetaData::getTableName, Function.identity()));
	}

	@Override
	public Optional<TableStructure> getTableStructure(String tableName) {
		return Optional.ofNullable(tableNameToStructure.get(tableName));
	}

	private Collection<TableStructure> createTablesStructures() {
		var usersStructure = usersStructure();
		var contactsStructure = contactsStructure();

		TableStructure.registerForeignKeyConstraint(
				new ColumnIdentifier(usersStructure, "ID"),
				new ColumnIdentifier(contactsStructure, "USER_ID"));

		return Stream.of(usersStructure, contactsStructure)
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
