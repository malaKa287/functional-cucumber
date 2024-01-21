package com.bdd.database.schema.table;

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.RowOutOfBoundsException;

import lombok.Getter;

@Getter
public class AbstractTableWrapper extends AbstractTable {

	private final TableStructure tableStructure;
	private final List<Map<String, String>> dataTableList;

	public AbstractTableWrapper(TableStructure tableStructure, List<Map<String, String>> dataTableList) {
		this.tableStructure = tableStructure;
		this.dataTableList = dataTableList;
	}

	@Override
	public ITableMetaData getTableMetaData() {
		return tableStructure;
	}

	@Override
	public int getRowCount() {
		return dataTableList.size();
	}

	@Override
	public Object getValue(int idx, String column) throws DataSetException {
		try {
			return dataTableList.get(idx).get(column.toUpperCase());
		}
		catch (Exception e) {
			throw new RowOutOfBoundsException(e);
		}
	}
}
