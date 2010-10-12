package org.serviceconnector.test.integration;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.integration.cln.AttachConnectionTypeHttpTest;
import org.serviceconnector.test.integration.cln.AttachConnectionTypeTcpTest;
import org.serviceconnector.test.integration.cln.AttachDetachTest;
import org.serviceconnector.test.integration.cln.AttachToMultipleSCTest;
import org.serviceconnector.test.integration.cln.EnableDisableServiceTest;
import org.serviceconnector.test.integration.cln.NewServicesTest;
import org.serviceconnector.test.integration.cln.PrematureDestroyOfSCProcessClientTest;
import org.serviceconnector.test.integration.cln.RestartOfSCProcessClientTest;
import org.serviceconnector.test.integration.srv.PrematureDestroyOfSCProcessServerTest;
import org.serviceconnector.test.integration.srv.PublishConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.PublishConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerToMultipleSCTest;
import org.serviceconnector.test.integration.srv.RestartSCProcessTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AttachToMultipleSCTest.class,
		AttachConnectionTypeTcpTest.class, AttachConnectionTypeHttpTest.class,
		AttachDetachTest.class, EnableDisableServiceTest.class,
		NewServicesTest.class, PrematureDestroyOfSCProcessClientTest.class,
		PrematureDestroyOfSCProcessServerTest.class,
		RegisterServerDeregisterServerConnectionTypeHttpTest.class,
		RegisterServerDeregisterServerConnectionTypeTcpTest.class,
		RegisterServerToMultipleSCTest.class,
		RegisterServerConnectionTypeHttpTest.class, RegisterServerConnectionTypeTcpTest.class,
		RestartOfSCProcessClientTest.class, RestartSCProcessTest.class,
		
		PublishConnectionTypeHttpTest.class, PublishConnectionTypeTcpTest.class
})
public class AllIntegrationTests {
}
