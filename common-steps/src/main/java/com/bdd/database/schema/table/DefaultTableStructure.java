package com.bdd.database.schema.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;

import io.cucumber.datatable.DataTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTableStructure implements TableStructure {

	private final TableIdentifier tableIdentifier;
	private final List<Column> columns;
	private final Map<String, Integer> columnNameToIndex;
	private final List<String> primaryKeys;
	private final List<ForeignKey> foreignKeys;
	private final CleanupStrategyType cleanupStrategyType;
	private final ConstraintActionType constraintActionType;

	public DefaultTableStructure(String schemaName, String tableName, Column... columns) {
		this(schemaName, tableName, CleanupStrategyType.SCENARIO, ConstraintActionType.CASCADE, columns);
	}

	public DefaultTableStructure(String schemaName, String tableName, CleanupStrategyType cleanupStrategyType,
			ConstraintActionType constraintActionType, Column... columns) {
		this.tableIdentifier = new TableIdentifier(schemaName, tableName);
		this.columns = Stream.of(columns).toList();
		this.columnNameToIndex = new HashMap<>();
		this.primaryKeys = List.of(this.columns.get(0).getColumnName());
		this.foreignKeys = new ArrayList<>();
		this.cleanupStrategyType = cleanupStrategyType;
		this.constraintActionType = constraintActionType;
		this.columns.forEach(el -> this.columnNameToIndex.put(el.getColumnName().toUpperCase(), this.columns.indexOf(el)));
	}

	@Override
	public String getTableName() {
		return tableIdentifier.tableName();
	}

	@Override
	public TableIdentifier getTableIdentifier() {
		return tableIdentifier;
	}

	@Override
	public Collection<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	@Override
	public CleanupStrategyType getCleanupStrategyType() {
		return cleanupStrategyType;
	}

	@Override
	public ConstraintActionType getConstraintActionType() {
		return constraintActionType;
	}

	@Override
	public Column[] getColumns() {
		return columns.toArray(new Column[0]);
	}

	@Override
	public Column[] getPrimaryKeys() {
		return columns.stream()
				.filter(column -> primaryKeys.contains(column.getColumnName()))
				.toArray(Column[]::new);
	}

	@Override
	public int getColumnIndex(String columnName) {
		return columnNameToIndex.get(columnName.toUpperCase());
	}

	public static IDataSet fromDataTable(TableStructure tableStructure, DataTable dataTable) {
		var dataTableList = dataTable.entries();
		try {
			return new DefaultDataSet(new AbstractTableWrapper(tableStructure, dataTableList));
		}
		catch (Exception e) {
			var identifier = tableStructure.getTableIdentifier();
			throw new IllegalStateException(String.format("Can't convert provided dataTable to [%s.%s] IDataSet.",
					identifier.schemaName(), identifier.tableName()), e);
		}
	}
}
