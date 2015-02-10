package org.george.gorelishvili.circuitebreaker;

public class CircuitBreakerConfig {

	private CircuitBreakerState state = CircuitBreakerState.CLOSED;

	private int maxTimeoutFail = 3; // open close after 3 fail
	private int timeoutCount;

	private long openStateTimeout = 10 * 60 * 1000; // 10 second

	private long maxExecTime = 30 * 60 * 60 * 1000; // 30 minute
	private long lastOpenStateTime;

	public CircuitBreakerConfig() {}

	public CircuitBreakerState getState() {
		return state;
	}

	public void setState(CircuitBreakerState state) {
		this.state = state;
	}

	public int getMaxTimeoutFail() {
		return maxTimeoutFail;
	}

	public void setMaxTimeoutFail(int maxTimeoutFail) {
		this.maxTimeoutFail = maxTimeoutFail;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	public void setTimeoutCount(int timeoutCount) {
		this.timeoutCount = timeoutCount;
	}

	public long getOpenStateTimeout() {
		return openStateTimeout;
	}

	public void setOpenStateTimeout(long openStateTimeout) {
		this.openStateTimeout = openStateTimeout;
	}

	public long getMaxExecTime() {
		return maxExecTime;
	}

	public void setMaxExecTime(long maxExecTime) {
		this.maxExecTime = maxExecTime;
	}

	public long getLastOpenStateTime() {
		return lastOpenStateTime;
	}

	public void setLastOpenStateTime(long lastOpenStateTime) {
		this.lastOpenStateTime = lastOpenStateTime;
	}

}
