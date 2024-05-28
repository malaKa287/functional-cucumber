package com.bdd.database.schema;

import java.util.Collection;
import java.util.Optional;

import com.bdd.database.connection.DatabaseConnectionData;
import com.bdd.database.schema.table.TableIdentifier;
import com.bdd.database.schema.table.TableStructure;

public interface DatabaseSchema {

	Optional<TableStructure> getTableStructure(TableIdentifier tableName);

	Collection<TableStructure> getTablesStructures();

	DatabaseConnectionData getConnectionData();
}
