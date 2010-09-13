package integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachClientToMultipleSCTest.class,
		AttachClientToSCConnectionTypeTCPTest.class, AttachClientToSCTest.class,
		AttachDetachClientToSCTest.class, EnableDisableServiceClientToSCTest.class,
		NewServicesClientToSCTest.class, PrematureDestroyOfSCClientToSCTest.class,
		PrematureDestroyOfSCServerToSCTest.class,
		RegisterServiceDeregisterServiceServerToSCConnectionTypeHttpTest.class,
		RegisterServiceDeregisterServiceServerToSCTest.class,
		RegisterServiceServerToMultipleSCTest.class,
		RegisterServiceServerToSCConnectionTypeHttpTest.class, RegisterServiceServerToSCTest.class,
		RestartSCClientToSCTest.class, RestartSCServerToSCTest.class
})
public class AllIntegrationTests {
}
