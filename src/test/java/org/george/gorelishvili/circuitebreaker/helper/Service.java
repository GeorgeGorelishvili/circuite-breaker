package org.george.gorelishvili.circuitebreaker.helper;

public class Service {

	public void call(boolean success) throws Exception {
		if (success) {
			Thread.sleep(C.SUCCESS_TIMEOUT);
		} else {
			Thread.sleep(C.FAILED_TIMEOUT);
		}
	}
}
