package com.bdd.database.connection;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public record DatabaseConnectionData(String username,
									 String password,
									 @NonNull String connectionUrl,
									 @NonNull DriverType driverType,
									 String schemaName) {

	public DatabaseConnectionData(String connectionUrl, DriverType driverType, @Nullable String schemaName) {
		this(null, null, connectionUrl, driverType, schemaName);
	}
}
