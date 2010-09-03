package integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachClientToSCTest.class, AttachDetachClientToSCTest.class,
		AttachClientToSCConnectionTypeTCPTest.class, AttachClientToMultipleSCTest.class,
		EnableDisableServiceClientToSCTest.class, NewServicesClientToSCTest.class,
		RegisterServiceServerToSCTest.class, RegisterServiceDeregisterServiceServerToSCTest.class,
		RegisterServiceDeregisterServiceServerToSCConnectionTypeHttpTest.class,
		RegisterServiceServerToSCConnectionTypeHttpTest.class,
		RegisterServiceServerToMultipleSCTest.class, PrematureDestroyOfSCClientToSCTest.class,
		PrematureDestroyOfSCServerToSCTest.class })
public class AllIntegrationTests {
}
