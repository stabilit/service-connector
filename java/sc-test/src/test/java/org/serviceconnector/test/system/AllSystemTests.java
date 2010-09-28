package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.perf.PerformanceTests;
import org.serviceconnector.test.system.publish.ChangeSubscriptionClientToSCTest;
import org.serviceconnector.test.system.publish.PublishClientTest;
import org.serviceconnector.test.system.publish.SubscribeClientToSCTest;
import org.serviceconnector.test.system.publish.SubscribeUnsubscribeClientTest;
import org.serviceconnector.test.system.publish.SubscriptionServerTest;
import org.serviceconnector.test.system.session.AsynchronousExecuteClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionHttpClientToSCTest;
import org.serviceconnector.test.system.session.CreateSessionTcpClientToSCTest;
import org.serviceconnector.test.system.session.ExecuteClientToSCTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfSCProcessClientTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfServerProcessClientTest;
import org.serviceconnector.test.system.session.RejectSessionClientToSCTest;
import org.serviceconnector.test.system.session.SessionServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// session tests
		CreateSessionHttpClientToSCTest.class, CreateSessionTcpClientToSCTest.class,
		ExecuteClientToSCTest.class, AsynchronousExecuteClientToSCTest.class,
		PrematureDestroyOfSCProcessClientTest.class, PrematureDestroyOfServerProcessClientTest.class,
		RejectSessionClientToSCTest.class,
		SessionServerTest.class,

		// publish tests
		SubscribeClientToSCTest.class, SubscribeUnsubscribeClientTest.class,
		ChangeSubscriptionClientToSCTest.class, PublishClientTest.class,
		SubscriptionServerTest.class,

		// special tests
		EnableServiceDisableServiceClientToSCTest.class,

		// performance tests
		PerformanceTests.class })
public class AllSystemTests {
}
