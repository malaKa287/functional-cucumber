package com.bdd.database.validator;

public enum DbValidationType {

	/**
	 * Validates if DB table contains expected data.
	 */
	SOFT,
	/**
	 * Validates if DB table equals expected data.
	 */
	STRICT;
}
