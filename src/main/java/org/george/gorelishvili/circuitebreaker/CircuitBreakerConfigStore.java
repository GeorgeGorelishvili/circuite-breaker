package org.george.gorelishvili.circuitebreaker;

import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerConfigStore {

	private static final ConcurrentHashMap<String, CircuitBreakerConfig> circuitBreakerConfigStore = new ConcurrentHashMap<>();

	static synchronized CircuitBreakerConfig getConfig(String key) {
		CircuitBreakerConfig config = circuitBreakerConfigStore.get(key);
		if (config == null) {
			return new CircuitBreakerConfig();
		}
		return config;
	}

	static synchronized void putConfig(String key, CircuitBreakerConfig config) {
		circuitBreakerConfigStore.put(key, config);
	}
}
