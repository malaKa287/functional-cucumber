package com.bdd.database.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.springframework.stereotype.Component;

import com.bdd.database.schema.table.AbstractTableWrapper;
import com.bdd.database.schema.table.TableStructure;

import io.cucumber.datatable.DataTable;

@Component
public class DatabaseTableConverter {

	public static IDataSet fromDataTable(TableStructure tableStructure, DataTable dataTable) {
		var dataTableList = dataTable.entries();
		try {
			return new DefaultDataSet(new AbstractTableWrapper(tableStructure, dataTableList));
		}
		catch (Exception e) {
			var identifier = tableStructure.getTableIdentifier();
			throw new IllegalStateException("Can't convert provided dataTable to [%s] IDataSet.".formatted(identifier), e);
		}
	}

	public List<Map<String, String>> asRowsMaps(ITable iTable) {
		return IntStream.range(0, iTable.getRowCount())
				.collect(ArrayList::new,
						(rows, rowIdx) -> {
							try {
								accumulateRows(rows, iTable, rowIdx);
							}
							catch (DataSetException e) {
								throw new IllegalArgumentException("Can't convert [%s] to the List<Map<String, String>>"
										.formatted(iTable.getTableMetaData().getTableName()), e);
							}
						},
						ArrayList::addAll);
	}

	private void accumulateRows(List<Map<String, String>> rows, ITable iTable, int rowIdx) throws DataSetException {
		Map<String, String> rowsMap = new HashMap<>();
		Arrays.stream(iTable.getTableMetaData().getColumns()).forEach(column -> {
			try {
				var rowValue = Optional.ofNullable(iTable.getValue(rowIdx, column.getColumnName()))
						.map(Object::toString)
						.orElse(null);
				rowsMap.put(column.getColumnName().toUpperCase(), rowValue);
			}
			catch (DataSetException e) {
				throw new IllegalArgumentException("Can't find a value for row: [%s] column: [%s]"
						.formatted(rowIdx, column.getColumnName().toUpperCase()), e);
			}
		});
		rows.add(rowsMap);
	}
}
