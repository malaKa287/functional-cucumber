package com.bdd.database.schema;

import java.util.Collection;
import java.util.Optional;

import com.bdd.database.schema.table.TableStructure;

public interface DatabaseSchema {

	String getSchemaName();

	Optional<TableStructure> getTableStructure(String tableName);

	Collection<TableStructure> getTablesStructures();

}
