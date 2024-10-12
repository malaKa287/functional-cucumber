package com.bdd.database.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.bdd.database.schema.DatabaseSchema;

@Component
public class DatabaseConnectionProvider {

	public final Map<String, DatabaseConnector> connectionsCache = new ConcurrentHashMap<>();

	public DatabaseConnector getConnection(DatabaseSchema databaseSchema) {
		var data = databaseSchema.getConnectionData();
		return connectionsCache.computeIfAbsent(data.connectionUrl(),
				url -> new DatabaseConnector(data.schemaName(), data.username(), data.password(), data.connectionUrl(), data.driverType()));
	}

}
