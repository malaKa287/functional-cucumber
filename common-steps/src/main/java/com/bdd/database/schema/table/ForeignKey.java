package com.bdd.database.schema.table;

public record ForeignKey(ColumnIdentifier sourceColumn, ColumnIdentifier dependantColumn) {
}
