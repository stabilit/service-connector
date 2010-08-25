package unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { SCClientTest.class, SCServerTest.class, SCMessageTest.class,
		SCServerStartListenerTest.class, SCServerDestroyServerTest.class })
public class AllUnitTests {
}
