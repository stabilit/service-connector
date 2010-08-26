package integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachClientToSCTest.class, AttachDetachClientToSCTest.class,
		AttachClientToSCConnectionTypeTCPTest.class, AttachClientToMultipleSCTest.class,
		RegisterServiceServerToSCTest.class, RegisterServiceDeregisterServiceServerToSCTest.class,
		RegisterServiceServerToSCConnectionTypeHttpTest.class})
public class AllIntegrationTests {
}
