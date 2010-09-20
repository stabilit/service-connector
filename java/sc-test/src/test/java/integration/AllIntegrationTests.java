package integration;

import integration.cln.AttachToMultipleSCTest;
import integration.cln.AttachConnectionTypeTcpTest;
import integration.cln.AttachConnectionTypeHttpTest;
import integration.cln.AttachDetachTest;
import integration.cln.EnableDisableServiceTest;
import integration.cln.NewServicesTest;
import integration.cln.PrematureDestroyOfSCClnTest;
import integration.cln.RestartOfSCClnTest;
import integration.srv.PrematureDestroyOfSCSrvTest;
import integration.srv.RegisterServiceDeregisterServiceConnectionTypeHttpTest;
import integration.srv.RegisterServiceDeregisterServiceConnectionTypeTcpTest;
import integration.srv.RegisterServiceToMultipleSCTest;
import integration.srv.RegisterServiceConnectionTypeHttpTest;
import integration.srv.RegisterServiceConnectionTypeTcpTest;
import integration.srv.RestartSCServerToSCTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachToMultipleSCTest.class,
		AttachConnectionTypeTcpTest.class, AttachConnectionTypeHttpTest.class,
		AttachDetachTest.class, EnableDisableServiceTest.class,
		NewServicesTest.class, PrematureDestroyOfSCClnTest.class,
		PrematureDestroyOfSCSrvTest.class,
		RegisterServiceDeregisterServiceConnectionTypeHttpTest.class,
		RegisterServiceDeregisterServiceConnectionTypeTcpTest.class,
		RegisterServiceToMultipleSCTest.class,
		RegisterServiceConnectionTypeHttpTest.class, RegisterServiceConnectionTypeTcpTest.class,
		RestartOfSCClnTest.class, RestartSCServerToSCTest.class
})
public class AllIntegrationTests {
}
