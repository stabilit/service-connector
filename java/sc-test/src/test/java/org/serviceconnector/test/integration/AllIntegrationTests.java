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
import org.serviceconnector.test.integration.srv.PublishConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.PublishConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerToMultipleSCTest;
import org.serviceconnector.test.integration.srv.RestartSCServerToSCTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachToMultipleSCTest.class,
		AttachConnectionTypeTcpTest.class, AttachConnectionTypeHttpTest.class,
		AttachDetachTest.class, EnableDisableServiceTest.class,
		NewServicesTest.class, PrematureDestroyOfSCClnTest.class,
		PrematureDestroyOfSCSrvTest.class,
		RegisterServerDeregisterServerConnectionTypeHttpTest.class,
		RegisterServerDeregisterServerConnectionTypeTcpTest.class,
		RegisterServerToMultipleSCTest.class,
		RegisterServerConnectionTypeHttpTest.class, RegisterServerConnectionTypeTcpTest.class,
		RestartOfSCClnTest.class, RestartSCServerToSCTest.class,
		
		PublishConnectionTypeHttpTest.class, PublishConnectionTypeTcpTest.class
})
public class AllIntegrationTests {
}
