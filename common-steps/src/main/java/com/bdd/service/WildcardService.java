package com.bdd.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class WildcardService {

	private final Map<String, String> wildcards = new HashMap<>();

	public void addOrUpdate(String key, String value) {
		wildcards.put(key, value);
	}

	public String getFor(String key) {
		return wildcards.get(key);
	}
}
