package com.bdd.database.schema.table;

import java.util.Collection;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITableMetaData;

public interface TableStructure extends ITableMetaData {

	/**
	 * @param parent column being referred by the child
	 * @param child  column targeting the parent
	 */
	static void registerForeignKeyConstraint(ColumnIdentifier parent, ColumnIdentifier child) {
		parent.structure().getForeignKeys()
				.add(new ForeignKey(parent, child));
	}

	TableIdentifier getTableIdentifier();

	Collection<ForeignKey> getForeignKeys();

	Column[] getColumns();

	CleanupStrategyType getCleanupStrategyType();

	ConstraintActionType getConstraintActionType();
}
