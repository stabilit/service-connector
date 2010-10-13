package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.perf.AllPerformanceTests;
import org.serviceconnector.test.system.publish.ChangeSubscriptionClientTest;
import org.serviceconnector.test.system.publish.PublishClientTest;
import org.serviceconnector.test.system.publish.SubscribeClientTest;
import org.serviceconnector.test.system.publish.SubscribeUnsubscribeClientTest;
import org.serviceconnector.test.system.publish.SubscriptionServerTest;
import org.serviceconnector.test.system.session.AsynchronousExecuteClientTest;
import org.serviceconnector.test.system.session.CreateSessionHttpClientTest;
import org.serviceconnector.test.system.session.CreateSessionTcpClientTest;
import org.serviceconnector.test.system.session.ExecuteClientTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfSCProcessClientTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfServerProcessClientTest;
import org.serviceconnector.test.system.session.RejectSessionClientTest;
import org.serviceconnector.test.system.session.SessionServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// session tests
		CreateSessionHttpClientTest.class, CreateSessionTcpClientTest.class,
		ExecuteClientTest.class, AsynchronousExecuteClientTest.class,
		PrematureDestroyOfSCProcessClientTest.class, PrematureDestroyOfServerProcessClientTest.class,
		RejectSessionClientTest.class,
		SessionServerTest.class,

		// publish tests
		SubscribeClientTest.class, SubscribeUnsubscribeClientTest.class,
		ChangeSubscriptionClientTest.class, PublishClientTest.class,
		SubscriptionServerTest.class,

		// special tests
		EnableServiceDisableServiceClientToSCTest.class})
public class AllSystemTests {
}
