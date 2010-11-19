package org.serviceconnector.test.console;

import static org.junit.Assert.assertEquals;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.console.SCConsole;
import org.serviceconnector.net.ConnectionType;

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
		System.setSecurityManager(null);
	}

	/**
	 * Description: start console with null parameter<br>
	 * (no arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t01_start() throws Exception {
		try {
			SCConsole.main(null);
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with empty string array = no parameters<br>
	 * (no arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t02_start() throws Exception {
		try {
			SCConsole.main(new String[] {});
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with empty parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t03_start() throws Exception {
		try {
			SCConsole.main(new String[] { "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with 5 empty parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t04_start() throws Exception {
		try {
			SCConsole.main(new String[] { "", "", "", "", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with 1 blank parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t05_start() throws Exception {
		try {
			SCConsole.main(new String[] { " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with 5 blank parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t06_start() throws Exception {
		try {
			SCConsole.main(new String[] { " ", " ", " ", " ", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "h" parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t07_start() throws Exception {
		try {
			SCConsole.main(new String[] { "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h" parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t08_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h" and empty parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t09_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h" and blank parameter<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t10_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h h" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t11_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t12_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p" and empty parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t13_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p" and blank parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t14_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", " " });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p h" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t15_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-p -h h" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t16_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-p", "-h", "h" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h h -p something" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t17_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h", "-p", "something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p h something" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t18_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h", "something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p h key=something" parameters<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t19_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h h -p key=something" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t20_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h", "-p", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -h h -p p key=something" parameters<br>
	 * (too many arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t21_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-h", "h", "-p", "p", "key=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 7000 state=something state=something" parameters<br>
	 * (too many arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t22_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_HTTP,
					Constants.STATE + "=something", Constants.STATE + "=something" });
		} catch (ExitException e) {
			assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 7000 state=something" parameters<br>
	 * (HTTP port)<br>
	 * Expectation: throws exception with exitCode = 5 "Communication error" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t23_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_HTTP,
					Constants.STATE + "=something" });
		} catch (ExitException e) {
			assertEquals(5, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 81 state=something" parameters<br>
	 * (management port)<br>
	 * Expectation: throws exception with exitCode = 5 "Communication error" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t24_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_MGMT,
					Constants.STATE + "=something" });
		} catch (ExitException e) {
			assertEquals(5, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 state=gaga" parameters<br>
	 * (unknown service name)<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t25_state() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, Constants.STATE + "=gaga" });
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h 127.0.0.1 -p 9000 state=session-1" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t26_state() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "127.0.0.1", "-p", TestConstants.PORT_SC_TCP, Constants.STATE + "=session-1" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: disable and re-enable the session service "session-1"<br>
	 * Expectation: passes though with exitCode = 0 "Success".<br>
	 * Pre-condition: SC must be running!<br>
	 * Post-condition: session service "session-1" is enabled again
	 */
	@Test
	public void t50_enable_disable_command() throws Exception {
		SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, Integer.parseInt(TestConstants.PORT_SC_TCP),
				ConnectionType.NETTY_TCP);
		client.attach();
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.DISABLE + "=session-1" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		assertEquals(false, client.isServiceEnabled("session-1"));
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.ENABLE + "=session-1" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		assertEquals(true, client.isServiceEnabled("session-1"));
		client.detach();
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=session-1" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t52_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=session-1" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=publish-1<br>
	 * Expectation: passes though with exitCode = 0 "Success" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t53_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=publish-1" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=notExistingService<br>
	 * (unknown service name)<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service"<br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t54_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=notExistingService" });
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga=notExistingService<br>
	 * (unknown command)<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command"<br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t55_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, "gaga=notExistingService" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga=session-1<br>
	 * (unknown command)<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command" <br>
	 * Pre-condition: SC must be running!
	 */
	@Test
	public void t56_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, "gaga=session-1" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 kill<br>
	 * Expectation: passes though with exitCode = 0 "Success" <br>
	 * Pre-condition: SC must be running!<br>
	 * Post-condition: SC will be killed!
	 */
	@Test
	public void t99_kill_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP, Constants.KILL });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}
}
