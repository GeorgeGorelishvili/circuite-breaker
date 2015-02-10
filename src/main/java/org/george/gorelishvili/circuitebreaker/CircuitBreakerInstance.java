package org.george.gorelishvili.circuitebreaker;

public class CircuitBreakerInstance {

	private static ThreadLocal<CircuitBreaker> context = new ThreadLocal<>();

	static void update(CircuitBreaker circuitBreaker) {
		context.set(circuitBreaker);
	}

	static CircuitBreaker get() {
		return context.get();
	}
}
