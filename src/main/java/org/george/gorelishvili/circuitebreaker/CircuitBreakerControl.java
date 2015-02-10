package org.george.gorelishvili.circuitebreaker;

public interface CircuitBreakerControl {

	boolean canCall();

	void endCall();
}