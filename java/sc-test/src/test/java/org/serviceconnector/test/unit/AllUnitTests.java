package org.serviceconnector.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { SCClientTest.class, SCServerTest.class, SCMessageTest.class,
		SCSessionServerTest.class })
public class AllUnitTests {
}
