package system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import system.publish.SubscribeClientToSCTest;
import system.session.CreateSessionHttpClientToSCTest;
import system.session.CreateSessionTcpClientToSCTest;
import system.session.ExecuteClientToSCTest;
import system.session.PrematureDestroyOfSCClientToSCTest;
import system.session.PrematureDestroyOfServerClientToSCTest;
import system.session.RejectSessionClientToSCTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CreateSessionHttpClientToSCTest.class,
	CreateSessionTcpClientToSCTest.class, EnableServiceDisableServiceClientToSCTest.class,
	ExecuteClientToSCTest.class, PerformanceTests.class, PrematureDestroyOfSCClientToSCTest.class,
	PrematureDestroyOfServerClientToSCTest.class, RejectSessionClientToSCTest.class,
	
	SubscribeClientToSCTest.class
})
public class AllSystemTests {
}
