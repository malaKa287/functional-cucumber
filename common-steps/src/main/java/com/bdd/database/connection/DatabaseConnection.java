package com.bdd.database.connection;

import java.sql.DriverManager;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

import lombok.NonNull;

public class DatabaseConnection extends AbstractDatabaseTester {

	private final String username;
	private final String password;
	private final String connectionUrl;
	private final DriverType driverType;

	public DatabaseConnection(String schema, String username, String password, @NonNull String connectionUrl, DriverType driverType) {
		super(schema);
		this.username = username;
		this.password = password;
		this.connectionUrl = connectionUrl;
		this.driverType = driverType;
	}

	@Override
	public IDatabaseConnection getConnection() {
		try {
			var dbConnection = username == null && password == null
					? DriverManager.getConnection(connectionUrl)
					: DriverManager.getConnection(connectionUrl, username, password);
			var connection = new org.dbunit.database.DatabaseConnection(dbConnection, getSchema());

			enrichWithDatatypeFactory(driverType, connection);
			return connection;
		}
		catch (Exception e) {
			throw new IllegalStateException("Can't create a connection for: " + connectionUrl, e);
		}
	}

	private void enrichWithDatatypeFactory(DriverType driverType, org.dbunit.database.DatabaseConnection databaseConnection)
			throws IllegalArgumentException {
		switch (driverType) {
			case ORACLE -> {
				setProperty(databaseConnection, DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new Oracle10DataTypeFactory());
				setProperty(databaseConnection, DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "\"");
			}
			case POSTGRES -> setProperty(databaseConnection, DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
			case MYSQL -> setProperty(databaseConnection, DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
			case MSSQL -> {
				setProperty(databaseConnection, DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
				setProperty(databaseConnection, DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "[?]");
			}
			default -> throw new IllegalArgumentException("Unexpected driver: " + driverType);
		}
	}

	private void setProperty(org.dbunit.database.DatabaseConnection connection, String name, Object value) {
		connection.getConfig().setProperty(name, value);
	}
}
