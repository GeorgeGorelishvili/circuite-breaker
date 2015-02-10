package org.george.gorelishvili.circuitebreaker.helper;

import org.george.gorelishvili.circuitebreaker.CircuitBreakerConfig;

public class CBLogger {
	public static void logState(CircuitBreakerConfig c, String message) {
		printWidthPadding("INFO: " + message);
		printWidthPadding("MaxExecTime: " + c.getMaxExecTime());

		printWidthPadding("State: " + c.getState().name());
		printWidthPadding("OpenStateTimeout: " + String.valueOf(c.getOpenStateTimeout()));

		printWidthPadding("LastOpenStateTime: " + String.valueOf(c.getLastOpenStateTime()));

		printWidthPadding("MaxTimeoutFail: " + String.valueOf(c.getMaxTimeoutFail()));
		printWidthPadding("TimeoutCount: " + String.valueOf(c.getTimeoutCount()));
		System.out.println();
	}

	public static void printWidthPadding(String message) {
		System.out.println(getTabs() + message);
	}

	private static String getTabs() {
		String tabs = "";
		int i = 0;
		while (i++ < 5) {
			tabs += "	";
		}
		return tabs;
	}
}
