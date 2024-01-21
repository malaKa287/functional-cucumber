package com.bdd.database.service;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.stereotype.Component;

@Component
public class DatabaseOperationsExecutor {

	public void execute(DatabaseOperation operation, IDatabaseConnection connection, IDataSet data) {
		try {
			operation.execute(connection, data);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(String.format("Can't execute [%s].", operation.getClass().getSimpleName()), e);
		}
	}
}
