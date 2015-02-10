package org.george.gorelishvili.circuitebreaker;

import org.george.gorelishvili.circuitebreaker.helper.C;
import org.george.gorelishvili.circuitebreaker.helper.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestServiceCall {

	private static final String TEST_SERVICE_CALL_KEY = "TEST_SERVICE_CALL_KEY";
	private static final long MAX_EXEC_TIMEOUT = 60 * 60 * 60 * 1000;

	private static ExecutorService executor;

	@Before
	public void setUp() {
		executor = Executors.newSingleThreadExecutor();
	}

	@Test
	public void shouldChangeStateToOpenState() throws InterruptedException {
		executor.execute(getRunnableForShouldChangeStateToOpenState());
		executor.shutdown();
		executor.awaitTermination(5l, TimeUnit.SECONDS);

		CircuitBreakerConfig config = CircuitBreakerConfigStore.getConfig(TEST_SERVICE_CALL_KEY);
		Assert.assertEquals(config.getState(), CircuitBreakerState.OPEN);
		Assert.assertEquals(config.getTimeoutCount(), 2);
		Assert.assertEquals(config.getLastOpenStateTime() == 0, false);
	}

	@Test
	public void shouldHalfOpenState() {
		executor.execute(getRunnableForShouldHalfOpenState(true));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.execute(getRunnableForShouldHalfOpenState(false));
		executor.shutdown();
	}

	private Runnable getRunnableForShouldHalfOpenState(final boolean success) {
		final String key = "getRunnableForShouldHalfOpenState";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				CircuitBreaker cb = new CircuitBreakerManager.Builder(key)
						.maxExecTime(C.CONFIG.MAX_EXECUTION_TIME)
						.openStateTimeout(C.CONFIG.OPEN_STATE_TIMEOUT)
						.maxTimeoutFail(C.CONFIG.MAX_TIMEOUT_FAIL)
						.callback(new StateChangeCallback() {
							@Override
							public void onStateChange(CircuitBreakerState updatedState) {
								if (success) {
									Assert.assertEquals(updatedState, CircuitBreakerState.CLOSED);
								} else {
									Assert.assertEquals(updatedState, CircuitBreakerState.OPEN);
								}
							}
						})
						.get();
				cb.config.setState(CircuitBreakerState.OPEN);
				cb.config.setTimeoutCount(C.CONFIG.MAX_TIMEOUT_FAIL);
				cb.execTimeStart = System.currentTimeMillis() - (2 * C.CONFIG.OPEN_STATE_TIMEOUT);

				try {
					if (cb.canCall()) {
						new Service().call(success);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					CircuitBreakerConfig config = CircuitBreakerInstance.get().config;
					Assert.assertEquals(config.getState(), CircuitBreakerState.HALF_OPEN);
					Assert.assertEquals(config.getTimeoutCount(), C.CONFIG.MAX_TIMEOUT_FAIL);
					cb.endCall();
					config = CircuitBreakerInstance.get().config;
					if (success) {
						Assert.assertEquals(config.getState(), CircuitBreakerState.CLOSED);
						Assert.assertEquals(config.getTimeoutCount(), 0);
					} else {
						Assert.assertEquals(config.getState(), CircuitBreakerState.OPEN);
						Assert.assertEquals(config.getTimeoutCount(), C.CONFIG.MAX_TIMEOUT_FAIL);
					}
				}
			}
		};
		return runnable;
	}

	private Runnable getRunnableForShouldChangeStateToOpenState() {
		return new Runnable() {
			@Override
			public void run() {
				CircuitBreaker cb = new CircuitBreakerManager.Builder(TEST_SERVICE_CALL_KEY)
						.maxExecTime(C.CONFIG.MAX_EXECUTION_TIME)
						.maxTimeoutFail(C.CONFIG.MAX_TIMEOUT_FAIL)
						.get();
				cb.execTimeStart = System.currentTimeMillis() - C.CONFIG.MAX_EXECUTION_TIME;
				cb.config.setTimeoutCount(C.CONFIG.MAX_TIMEOUT_FAIL - 1);
				try {
					if (cb.canCall()) {
						new Service().call(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cb.endCall();
					CircuitBreakerConfig config = CircuitBreakerInstance.get().config;
					Assert.assertEquals(config.getState(), CircuitBreakerState.OPEN);
					Assert.assertEquals(config.getTimeoutCount(), C.CONFIG.MAX_TIMEOUT_FAIL);
					Assert.assertEquals(config.getLastOpenStateTime() == 0, false);
				}
			}
		};
	}

	@Test
	public void testDisableConfigurationUpdate() {
		final String key = "testDisableConfigurationUpdate";
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				CircuitBreaker cb = new CircuitBreakerManager.Builder(key)
						.maxExecTime(MAX_EXEC_TIMEOUT)
						.disableConfigurationUpdate()
						.callback(new StateChangeCallback() {
							@Override
							public void onStateChange(CircuitBreakerState updatedState) {
								System.out.println("kuku");
							}
						})
						.get();

				try {
					if (cb.canCall()) {
						new Service().call(false);
					}
				} catch (Exception ex) {

				} finally {
					cb.endCall();
					CircuitBreakerConfig config = CircuitBreakerInstance.get().config;
					Assert.assertEquals(config.getMaxExecTime(), MAX_EXEC_TIMEOUT);
				}
			}
		};

		CircuitBreaker cb = new CircuitBreakerManager.Builder(key).get();
		Assert.assertNotEquals(cb.config.getMaxExecTime(), MAX_EXEC_TIMEOUT);
		executor.execute(runnable);
		executor.shutdown();
	}
}
