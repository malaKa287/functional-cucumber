package com.bdd.database.schema.table;

public enum CleanupStrategyType {

	/**
	 * Clear DB table after each scenario.
	 */
	SCENARIO,
	/**
	 * DB content is persisted across scenarios.
	 */
	NONE
}
