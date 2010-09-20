package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.publish.SubscribeClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionHttpClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionTcpClientToSCTest;
import org.serviceconnector.test.system.session.ExecuteClientToSCTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfSCClientToSCTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfServerClientToSCTest;
import org.serviceconnector.test.system.session.RejectSessionClientToSCTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { CreateSessionHttpClientToSCTest.class,
	CreateSessionTcpClientToSCTest.class, EnableServiceDisableServiceClientToSCTest.class,
	ExecuteClientToSCTest.class, PerformanceTests.class, PrematureDestroyOfSCClientToSCTest.class,
	PrematureDestroyOfServerClientToSCTest.class, RejectSessionClientToSCTest.class,
	
	SubscribeClientToSCTest.class
})
public class AllSystemTests {
}
