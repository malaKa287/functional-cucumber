package com.bdd.database.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.bdd.database.schema.DatabaseSchema;

@Component
public class DatabaseConnectionProvider {

	public final Map<String, DatabaseConnection> connectionsCache = new ConcurrentHashMap<>();

	public DatabaseConnection getConnection(DatabaseSchema databaseSchema) {
		var data = databaseSchema.getConnectionData();
		return connectionsCache.computeIfAbsent(data.connectionUrl(),
				url -> new DatabaseConnection(data.schemaName(), data.username(), data.password(), data.connectionUrl(), data.driverType()));
	}

}
