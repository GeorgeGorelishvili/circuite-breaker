package org.george.gorelishvili.circuitebreaker.helper;

import java.util.HashMap;
import java.util.Map;

public class C {

	/**
	 * multiplier for timeouts
	 */
	public static final long SECOND = 10; // second

	/**
	 * map for emulation service result
	 */
	public static Map<Integer, Boolean> R = new HashMap<>();
	static {
		R.put(0, false);
		R.put(1, false);
		R.put(2, false);
		R.put(3, false);
		R.put(4, false);
		R.put(5, true);
		R.put(6, true);
		R.put(7, false);
		R.put(8, true);
		R.put(9, true);
		R.put(10, true);
	}

	/**
	 * service success timeout
	 */
	public static final long SUCCESS_TIMEOUT = 5 * C.SECOND;

	/**
	 * service fail timeout
	 */
	public static final long FAILED_TIMEOUT = 2 * SUCCESS_TIMEOUT;

	public static class CONFIG {

		/**
		 * max timeout fails
		 */
		public static final int MAX_TIMEOUT_FAIL = 2;

		/**
		 * when CB is in OPEN state, after OPEN_STATE_TIMEOUT CB.canCall() mast
		 * return true and CB must be in HALF_OPEN state
		 */
		public static final long OPEN_STATE_TIMEOUT = 2 * MAX_TIMEOUT_FAIL * FAILED_TIMEOUT; // second

		/**
		 * service mast end on normal case in MAX_EXECUTION_TIME period,
		 * then fails.
		 *
		 * must be less then SERVICE.FAILED_TIMEOUT
		 * must be more then SERVICE.SUCCESS_TIMEOUT
		 */
		public static final long MAX_EXECUTION_TIME = (SUCCESS_TIMEOUT + FAILED_TIMEOUT) / 2; // second
	}

	public static final String SUCCESS_SERVICE_TIME = "Success service time : " + SUCCESS_TIMEOUT / C.SECOND + " seconds and + max 10 millis";
	public static final String FAIL_SERVICE_TIME = "Failed service time: " + FAILED_TIMEOUT / C.SECOND + " seconds and + max 10 millis";
}
