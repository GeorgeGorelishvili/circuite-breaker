package org.george.gorelishvili.circuitebreaker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
 	InitialTest.class,
	TestServiceCall.class
})
public class JunitTestSuite {
}
