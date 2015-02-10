package org.george.gorelishvili.circuitebreaker;

public interface StateChangeCallback {

	void onStateChange(CircuitBreakerState updatedState);
}
