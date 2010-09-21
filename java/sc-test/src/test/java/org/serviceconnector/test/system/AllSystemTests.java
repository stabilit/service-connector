package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.perf.PerformanceTests;
import org.serviceconnector.test.system.publish.SubscribeClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionHttpClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionTcpClientToSCTest;
import org.serviceconnector.test.system.session.ExecuteClientToSCTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfSCClientToSCTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfServerClientToSCTest;
import org.serviceconnector.test.system.session.RejectSessionClientToSCTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { CreateSessionHttpClientToSCTest.class,
	//session tests
	CreateSessionTcpClientToSCTest.class, EnableServiceDisableServiceClientToSCTest.class,
	ExecuteClientToSCTest.class, PrematureDestroyOfSCClientToSCTest.class,
	PrematureDestroyOfServerClientToSCTest.class, RejectSessionClientToSCTest.class,
	
	//publish tests
	SubscribeClientToSCTest.class,
	
	//performance tests
	PerformanceTests.class 
})
public class AllSystemTests {
}
