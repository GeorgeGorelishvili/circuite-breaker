package org.george.gorelishvili.circuitebreaker;

import org.george.gorelishvili.circuitebreaker.helper.C;
import org.junit.Assert;
import org.junit.Test;

public class InitialTest {

	private static final long MAX_EXEC_TIME_CHANGE = 10 * 60 * 1000l; // 10 second
	private static final int MAX_TIMEOUT_FAIL_CHANGE = 5; // on 5 fail must change state
	private static final long OPEN_STATE_TIMEOUT_CHANGE = 30 * 60 * 60 * 1000l; // after 30 minute must change state from OPEN to HALF-OPEN

	@Test
	public void testDefaultInitialization() {
		CircuitBreaker cb = new CircuitBreakerManager.Builder("testDefaultInitialization")
				.maxExecTime(C.CONFIG.MAX_EXECUTION_TIME)
				.openStateTimeout(C.CONFIG.OPEN_STATE_TIMEOUT)
				.maxTimeoutFail(C.CONFIG.MAX_TIMEOUT_FAIL)
				.disableConfigurationUpdate()
				.callback(new StateChangeCallback() {
					@Override
					public void onStateChange(CircuitBreakerState state) {
						if (state == CircuitBreakerState.CLOSED) {

						}
						CircuitBreakerConfig config = CircuitBreakerInstance.get().config;
						Assert.assertEquals(config.getMaxExecTime(), C.CONFIG.MAX_EXECUTION_TIME);
						Assert.assertEquals(config.getOpenStateTimeout(), C.CONFIG.OPEN_STATE_TIMEOUT);
						Assert.assertEquals(config.getMaxTimeoutFail(), C.CONFIG.MAX_TIMEOUT_FAIL);
					}
				}).get();
		Assert.assertEquals(cb.config.getMaxExecTime(), C.CONFIG.MAX_EXECUTION_TIME);
		Assert.assertEquals(cb.config.getOpenStateTimeout(), C.CONFIG.OPEN_STATE_TIMEOUT);
		Assert.assertEquals(cb.config.getMaxTimeoutFail(), C.CONFIG.MAX_TIMEOUT_FAIL);
	}

	@Test
	public void testConfigurationChange() {
		String key = "testConfigurationChange";
		CircuitBreaker cb = new CircuitBreakerManager.Builder(key)
				.maxExecTime(MAX_EXEC_TIME_CHANGE)
				.maxTimeoutFail(MAX_TIMEOUT_FAIL_CHANGE)
				.openStateTimeout(OPEN_STATE_TIMEOUT_CHANGE)
				.get();

		// update config after call
		cb.endCall();

		CircuitBreakerConfig config = CircuitBreakerInstance.get().config;
		Assert.assertEquals(config.getMaxExecTime(), MAX_EXEC_TIME_CHANGE);
		Assert.assertEquals(config.getOpenStateTimeout(), OPEN_STATE_TIMEOUT_CHANGE);
		Assert.assertEquals(config.getMaxTimeoutFail(), MAX_TIMEOUT_FAIL_CHANGE);

		// get new
		new CircuitBreakerManager.Builder(key).get();

		config = CircuitBreakerInstance.get().config;
		Assert.assertEquals(config.getMaxExecTime(), MAX_EXEC_TIME_CHANGE);
		Assert.assertEquals(config.getOpenStateTimeout(), OPEN_STATE_TIMEOUT_CHANGE);
		Assert.assertEquals(config.getMaxTimeoutFail(), MAX_TIMEOUT_FAIL_CHANGE);
	}
}
