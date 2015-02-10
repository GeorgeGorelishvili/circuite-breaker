package org.george.gorelishvili.circuitebreaker;

public class CircuitBreaker implements CircuitBreakerControl {

	long execTimeStart;

	private String key;
	CircuitBreakerConfig config;
	boolean updateConfiguration = true;
	private StateChangeCallback callback;

	public CircuitBreaker(String key, CircuitBreakerConfig config) {
		this.key = key;
		this.config = config;
	}

	@Override
	public boolean canCall() {
		execTimeStart = System.currentTimeMillis();
		if (config.getState() == CircuitBreakerState.OPEN) {
			changeState(false);
		}
		return config.getState() != CircuitBreakerState.OPEN;
	}

	@Override
	public void endCall() {
		changeState(true);
		updateContext();
	}

	public String getKey() {
		return key;
	}

	void setCallback(StateChangeCallback callback) {
		this.callback = callback;
	}

	public void setUpdateConfiguration(boolean updateConfiguration) {
		this.updateConfiguration = updateConfiguration;
	}

	private void changeState(boolean isEndCall) {
		int timeoutCount = config.getTimeoutCount();
		CircuitBreakerState state = config.getState();
		boolean succeed = isSuccessCall();

		if (config.getState() == CircuitBreakerState.CLOSED) {
			timeoutCount = succeed ? 0 : ++timeoutCount;
			// when reached max fail timeout
			if (timeoutCount == config.getMaxTimeoutFail()) {
				state = CircuitBreakerState.OPEN;
				config.setLastOpenStateTime(System.currentTimeMillis());
			}
		}

		// open state timeout reached
		if (config.getState() == CircuitBreakerState.OPEN && isOpenTimeoutReached()) {
			state = CircuitBreakerState.HALF_OPEN;
		}

		if (config.getState() == CircuitBreakerState.HALF_OPEN) {
			if (succeed) {
				state = CircuitBreakerState.CLOSED;
				config.setLastOpenStateTime(System.currentTimeMillis());
				timeoutCount = 0;
			} else {
				state = CircuitBreakerState.OPEN;
			}
		}

		// print
		if (callback != null && isStateChanged(state) && isEndCall) {
			callback.onStateChange(state);
		}

		config.setState(state);
		config.setTimeoutCount(timeoutCount);
	}

	boolean isStateChanged(CircuitBreakerState state) {
		return state != config.getState();
	}

	boolean isOpenTimeoutReached() {
		return System.currentTimeMillis() > config.getLastOpenStateTime() + config.getOpenStateTimeout();
	}

	boolean isSuccessCall() {
		return config.getMaxExecTime() > System.currentTimeMillis() - execTimeStart;
	}

	private void updateContext() {
		CircuitBreakerInstance.update(this);
		if (updateConfiguration) {
			CircuitBreakerConfigStore.putConfig(key, config);
		}
	}
}
