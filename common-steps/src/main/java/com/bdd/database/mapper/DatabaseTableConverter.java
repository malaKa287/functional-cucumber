package com.bdd.database.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTableConverter {

	public List<Map<String, String>> asRowsMaps(ITable iTable) {
		return IntStream.range(0, iTable.getRowCount())
				.collect(ArrayList::new,
						(rows, rowIdx) -> {
							try {
								accumulateRows(rows, iTable, rowIdx);
							}
							catch (DataSetException e) {
								throw new IllegalArgumentException(String.format("Can't convert [%s] to the List<Map<String, String>>",
										iTable.getTableMetaData().getTableName()), e);
							}
						},
						ArrayList::addAll);
	}

	private void accumulateRows(List<Map<String, String>> rows, ITable iTable, int rowIdx) throws DataSetException {
		Map<String, String> rowsMap = new HashMap<>();
		Arrays.stream(iTable.getTableMetaData().getColumns()).forEach(column -> {
			try {
				String rowValue = Optional.ofNullable(iTable.getValue(rowIdx, column.getColumnName()))
						.map(Object::toString)
						.orElse(null);
				rowsMap.put(column.getColumnName().toUpperCase(), rowValue);
			}
			catch (DataSetException e) {
				throw new IllegalArgumentException(String.format("Can't find a value for row: [%s] column: [%s]", rowIdx,
						column.getColumnName().toUpperCase()), e);
			}
		});
		rows.add(rowsMap);
	}
}
