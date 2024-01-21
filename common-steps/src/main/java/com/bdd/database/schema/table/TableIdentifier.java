package com.bdd.database.schema.table;

public record TableIdentifier(String schemaName, String tableName) {

	@Override
	public String toString() {
		return schemaName + "." + tableName;
	}
}
