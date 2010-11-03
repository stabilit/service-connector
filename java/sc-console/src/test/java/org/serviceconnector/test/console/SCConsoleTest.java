package org.serviceconnector.test.console;

import static org.junit.Assert.assertEquals;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.console.ConsoleCommand;
import org.serviceconnector.console.SCConsole;


public class SCConsoleTest {

	protected static class ExitException extends SecurityException {
		public final int status;

		public ExitException(int status) {
			super("There is no escape!");
			this.status = status;
		}
	}

	private static class NoExitSecurityManager extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new ExitException(status);
		}
	}

	@Before
	public void setUp() throws Exception {
		System.setSecurityManager(new NoExitSecurityManager());
	}

	@After
	public void tearDown() throws Exception {
		System.setSecurityManager(null); // or save and restore original
	}

	/**
	 * Description:	start console with null parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_01() throws Exception {
		try {
			SCConsole.main(null);
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description:	start console with empty string array = no parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_02() throws Exception {
		try {
			SCConsole.main(new String[] {});
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description:	start console with empty parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_03() throws Exception {
		try {
			SCConsole.main(new String[] { "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description:	start console with 5 empty parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_04() throws Exception {
		try {
			SCConsole.main(new String[] { "", "", "", "", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with 1 blank parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_05() throws Exception {
		try {
			SCConsole.main(new String[] { " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description:	start console with 5 blank parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_06() throws Exception {
		try {
			SCConsole.main(new String[] { " ", " ", " ", " ", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "h" parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_07() throws Exception {
		try {
			SCConsole.main(new String[] { "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description:	start console with "-h" parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_08() throws Exception {
		try {
			SCConsole.main(new String[] { "-h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h" and empty parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_09() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h" and blank parameter<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_10() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h h" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_11() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_12() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p" and empty parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_13() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p" and blank parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_14() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p h" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_15() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-p -h h" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_16() throws Exception {
		try {
			SCConsole.main(new String[] { "-p", "-h", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h h -p something" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_17() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h", "-p", "something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p h something" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_18() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h", "something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -p h key=something" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_19() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h h -p key=something" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_20() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h", "-p", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h -h h -p p key=something" parameters<br>
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_21() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-h", "h", "-p", "p", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 7000 SHOW=something SHOW=something" parameters<br>
	 * multiple commands
	 * Expectation:	throws exception with exitCode = 1
	 */
	@Test
	public void main_22() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_HTTP, ConsoleCommand.STATE.getKey() + "=something", ConsoleCommand.STATE.getKey() + "=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 7000 SHOW=something" parameters<br>
	 * HTTP port
	 * Expectation:	throws exception with exitCode = 5 "Communication error"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_23() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_HTTP, ConsoleCommand.STATE.getKey() + "=something" });
		} catch (ExitException e) {
			assertEquals(5, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 81 SHOW=something" parameters<br>
	 * Management port
	 * Expectation:	throws exception with exitCode = 5 "Communication error"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_24() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_MGMT, ConsoleCommand.STATE.getKey() + "=something"});
		} catch (ExitException e) {
			assertEquals(5, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 SHOW=something" parameters<br>
	 * Expectation:	throws exception with exitCode = 4 "Unknown service"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_25() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.STATE.getKey() + "=something"});
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h 127.0.0.1 -p 9000 SHOW=simulation" parameters<br>
	 * Expectation:	passes though with exitCode = 0 "Success"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_26() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "127.0.0.1", "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.STATE.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 ENABLE=simulation" parameters<br>
	 * Expectation:	passes though with exitCode = 0 "Success", service is enabled
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_27_enable() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.ENABLE.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		SCMgmtClient client = new SCMgmtClient();
		client.setConnectionType(org.serviceconnector.Constants.NETTY_TCP);
		client.attach(TestConstants.HOST, Integer.parseInt(TestConstants.PORT_SC_TCP));
		assertEquals(true, client.isServiceEnabled("simulation"));
		client.detach();
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 DISABLE=simulation" parameters<br>
	 * Expectation:	passes though with exitCode = 0 "Success", service is disabled
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_28_disable() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.DISABLE.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		SCMgmtClient client = new SCMgmtClient();
		client.setConnectionType(org.serviceconnector.Constants.NETTY_TCP);
		client.attach(TestConstants.HOST, Integer.parseInt(TestConstants.PORT_SC_TCP));
		assertEquals(false, client.isServiceEnabled("simulation"));
		
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.ENABLE.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		assertEquals(true, client.isServiceEnabled("simulation"));
		client.detach();
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 SESSIONS=simulation" parameters<br>
	 * Expectation:	passes though with exitCode = 0 "Success"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_29_sessions() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.SESSIONS.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 SESSIONS=publish-simulation<br>
	 * Expectation:	passes though with exitCode = 0 "Success"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_30_sessions() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.SESSIONS.getKey() + "=publish-simulation" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 SESSIONS=notExistingService<br>
	 * Expectation:	throws exception with exitCode = 4 "Unknown service"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_31_sessions() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.SESSIONS.getKey() + "=notExistingService" });
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 UNDEFINED=notExistingService<br>
	 * Expectation:	throws exception with exitCode = 3 "invalid command"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_32_undefined() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.UNDEFINED.getKey() + "=notExistingService" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 UNDEFINED=simulation<br>
	 * Expectation:	throws exception with exitCode = 3 "invalid command"
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void main_33_undefined() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.UNDEFINED.getKey() + "=simulation" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}
	
	/**
	 * Description:	start console with "-h localhost -p 9000 KILL<br>
	 * Expectation:	passes though with exitCode = 0 "Success"
	 * Pre-condition: SC must be running!
	 * Post-condition: SC will be killed!
	 */
	@Test
	public void main_99_kill() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, ConsoleCommand.KILL.getKey()});
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}
}
