package system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CreateSessionHttpClientToSCTest.class,
	CreateSessionTcpClientToSCTest.class, EnableServiceDisableServiceClientToSCTest.class,
	ExecuteClientToSCTest.class, PerformanceTests.class,
	PrematureDestroyOfServerClientToSCTest.class, RejectSessionClientToCSTest.class
})
public class AllSystemTests {
}
