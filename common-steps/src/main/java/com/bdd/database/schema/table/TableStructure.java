package com.bdd.database.schema.table;

import java.util.Collection;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITableMetaData;

public interface TableStructure extends ITableMetaData {

	void registerForeignKeyConstraint(String sourceColumnName, ColumnIdentifier dependantColumnIdentifier);

	TableIdentifier getTableIdentifier();

	Collection<ForeignKey> getForeignKeys();

	Column[] getColumns();

	CleanupStrategyType getCleanupStrategyType();

	ConstraintActionType getConstraintActionType();
}
