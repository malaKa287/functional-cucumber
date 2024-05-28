package com.bdd.database.connection;

import lombok.NonNull;

public record DatabaseConnectionData(String username,
									 String password,
									 @NonNull String connectionUrl,
									 @NonNull DriverType driverType,
									 String schemaName) {
}
