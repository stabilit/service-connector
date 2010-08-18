package integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { attachClientToSCTest.class, })
public class AllIntegrationTests {
}
