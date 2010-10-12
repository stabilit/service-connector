package org.serviceconnector.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.integration.AllIntegrationTests;
import org.serviceconnector.test.system.AllSystemTests;
import org.serviceconnector.test.unit.AllUnitTests;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllUnitTests.class,
		AllIntegrationTests.class, AllSystemTests.class })
public class AllTests {
}
