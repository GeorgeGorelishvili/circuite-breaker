package org.george.gorelishvili.circuitebreaker;

public class CircuitBreakerManager {

	private CircuitBreakerManager() {}

	public static class Builder {
		private String key;
		private StateChangeCallback callback;
		private Integer maxTimeoutFail;
		private Long openStateTimeout;
		private Long maxExecTime;
		private boolean disableConfigurationUpdate;

		public Builder maxTimeoutFail(Integer timeOutFailCount) {
			this.maxTimeoutFail = timeOutFailCount;
			return this;
		}

		public Builder openStateTimeout(Long openStateTimeout) {
			this.openStateTimeout = openStateTimeout;
			return this;
		}

		public Builder maxExecTime(Long maxExecTime) {
			this.maxExecTime = maxExecTime;
			return this;
		}

		public Builder(String key) {
			this.key = key;
		}

		public Builder callback(StateChangeCallback callback) {
			this.callback = callback;
			return this;
		}

		public Builder disableConfigurationUpdate() {
			this.disableConfigurationUpdate = true;
			return this;
		}

		public CircuitBreaker get() {
			CircuitBreakerConfig config = CircuitBreakerConfigStore.getConfig(key);
			if (config == null) {
				config = new CircuitBreakerConfig();
			}

			if (maxTimeoutFail != null) {
				config.setMaxTimeoutFail(maxTimeoutFail);
			}

			if (maxExecTime != null) {
				config.setMaxExecTime(maxExecTime);
			}

			if (openStateTimeout != null) {
				config.setOpenStateTimeout(openStateTimeout);
			}

			CircuitBreaker cb = new CircuitBreaker(key, config);
			if (callback != null) {
				cb.setCallback(callback);
			}
			if (disableConfigurationUpdate) {
				cb.setUpdateConfiguration(!disableConfigurationUpdate);
			}
			CircuitBreakerInstance.update(cb);

			return CircuitBreakerInstance.get();
		}
	}
}
