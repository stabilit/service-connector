package org.serviceconnector.test.integration;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.integration.cln.AttachConnectionTypeHttpTest;
import org.serviceconnector.test.integration.cln.AttachConnectionTypeTcpTest;
import org.serviceconnector.test.integration.cln.AttachDetachTest;
import org.serviceconnector.test.integration.cln.AttachToMultipleSCTest;
import org.serviceconnector.test.integration.cln.EnableDisableServiceTest;
import org.serviceconnector.test.integration.cln.NewServicesTest;
import org.serviceconnector.test.integration.cln.PrematureDestroyOfSCClnTest;
import org.serviceconnector.test.integration.cln.RestartOfSCClnTest;
import org.serviceconnector.test.integration.srv.PrematureDestroyOfSCSrvTest;
import org.serviceconnector.test.integration.srv.RegisterServiceConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServiceConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServiceDeregisterServiceConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServiceDeregisterServiceConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServiceToMultipleSCTest;
import org.serviceconnector.test.integration.srv.RestartSCServerToSCTest;

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
