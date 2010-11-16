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
	 * Description: start console with "-h localhost -p 7000 state=something SHOW=something" parameters<br>
	 * multiple commands Expectation: throws exception with exitCode = 1
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
	 * HTTP port Expectation: throws exception with exitCode = 5 "Communication error" Pre-condition: SC must be running!
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
	 * Management port Expectation: throws exception with exitCode = 5 "Communication error" Pre-condition: SC must be running!
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
	 * Description: start console with "-h localhost -p 9000 state=something" parameters<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service" Pre-condition: SC must be running!
	 */
	@Test
	public void t25_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.STATE + "=something" });
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h 127.0.0.1 -p 9000 state=local-session-service" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success" Pre-condition: SC must be running!
	 */
	@Test
	public void t26_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "127.0.0.1", "-p", TestConstants.PORT_SC_TCP,
					Constants.STATE + "=local-session-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 enable=local-session-service" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success", service is enabled Pre-condition: SC must be running!
	 */
	@Test
	public void t27_enable_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.ENABLE + "=local-session-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, Integer.parseInt(TestConstants.PORT_SC_TCP),
				ConnectionType.NETTY_TCP);
		client.attach();
		assertEquals(true, client.isServiceEnabled("local-session-service"));
		client.detach();
	}

	/**
	 * Description: start console with "-h localhost -p 9000 disable=local-session-service" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success", service is disabled Pre-condition: SC must be running!
	 */
	@Test
	public void t28_disable_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.DISABLE + "=local-session-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, Integer.parseInt(TestConstants.PORT_SC_TCP),
				ConnectionType.NETTY_HTTP);
		client.attach();
		assertEquals(false, client.isServiceEnabled("local-session-service"));

		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.ENABLE + "=local-session-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
		assertEquals(true, client.isServiceEnabled("local-session-service"));
		client.detach();
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=local-session-service" parameters<br>
	 * Expectation: passes though with exitCode = 0 "Success" Pre-condition: SC must be running!
	 */
	@Test
	public void t29_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=local-session-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=local-publish-service<br>
	 * Expectation: passes though with exitCode = 0 "Success" Pre-condition: SC must be running!
	 */
	@Test
	public void t30_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=local-publish-service" });
		} catch (ExitException e) {
			assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions=notExistingService<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service" Pre-condition: SC must be running!
	 */
	@Test
	public void t31_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					Constants.SESSIONS + "=notExistingService" });
		} catch (ExitException e) {
			assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga=notExistingService<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command" Pre-condition: SC must be running!
	 */
	@Test
	public void t32_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					"gaga=notExistingService" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga=local-session-service<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command" Pre-condition: SC must be running!
	 */
	@Test
	public void t33_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", TestConstants.PORT_SC_TCP,
					"gaga=local-session-service" });
		} catch (ExitException e) {
			assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 kill<br>
	 * Expectation: passes though with exitCode = 0 "Success" Pre-condition: SC must be running! Post-condition: SC will be killed!
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
