/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.integration.console;

import java.security.Permission;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.console.SCConsole;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.IntegrationSuperTest;

public class SCConsoleTest extends IntegrationSuperTest {

	protected static class ExitException extends SecurityException {

		private static final long serialVersionUID = -8667013915273056665L;
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
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		System.setSecurityManager(new NoExitSecurityManager());
	}

	@After
	public void afterOneTest() throws Exception {
		System.setSecurityManager(null);
		super.afterOneTest();
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
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
			Assert.assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -p h key?serviceName=something" parameters<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t19_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-p", "h", "key?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h h -p key?serviceName=something" parameters<br>
	 * (not enough arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t20_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "h", "-p", "key?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h -h h -p p key?serviceName=something" parameters<br>
	 * (too many arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t21_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "-h", "h", "-p", "p", "key?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 7000 state?serviceName=something state=something" parameters<br>
	 * (too many arguments)<br>
	 * Expectation: throws exception with exitCode = 1
	 */
	@Test
	public void t22_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", String.valueOf(TestConstants.HOST), "-p",
					String.valueOf(TestConstants.PORT_SC_HTTP), Constants.CC_CMD_STATE + "?serviceName=something",
					Constants.CC_CMD_STATE + "?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(1, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 7000 state?serviceName=something" parameters<br>
	 * (HTTP port)<br>
	 * Expectation: throws exception with exitCode = 5 "Communication error" <br>
	 */
	@Test
	public void t23_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_HTTP),
					Constants.CC_CMD_STATE + "?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(5, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 81 state?serviceName=something" parameters<br>
	 * (management port)<br>
	 * Expectation: throws exception with exitCode = 5 "Communication error" <br>
	 */
	@Test
	public void t24_start() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_MGMT),
					Constants.CC_CMD_STATE + "?serviceName=something" });
		} catch (ExitException e) {
			Assert.assertEquals(5, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 state?serviceName=gaga" parameters<br>
	 * (unknown service name)<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service" <br>
	 */
	@Test
	public void t25_stateUnknownService() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_STATE + "?serviceName=gaga" });
		} catch (ExitException e) {
			Assert.assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h 127.0.0.1 -p 9000 state?serviceName=session-1" parameters<br>
	 * Expectation: passes with exitCode = 0 "Success" <br>
	 */
	@Test
	public void t26_state() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", "127.0.0.1", "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_STATE + "?serviceName=session-1" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: disable and re-enable the session service "session-1"<br>
	 * Expectation: passes with exitCode = 0 "Success".<br>
	 * Post-condition: session service "session-1" is enabled again
	 */
	@Test
	public void t50_enable_disable_command() throws Exception {
		SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_DISABLE + "?serviceName=session-1" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
		Assert.assertEquals(false, client.isServiceEnabled("session-1"));
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_ENABLE + "?serviceName=session-1" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
		Assert.assertEquals(true, client.isServiceEnabled("session-1"));
		client.detach();
	}

	/**
	 * Description: disable and re-enable all services<br>
	 * Expectation: passes with exitCode = 0 "Success".<br>
	 * Post-condition: session service "session-1" is enabled
	 */
	@Test
	public void t51_enable_disable_wildCard_command() throws Exception {
		SCMgmtClient client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_DISABLE + "?serviceName=*" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
		Assert.assertEquals(false, client.isServiceEnabled("session-1"));
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_ENABLE + "?serviceName=*" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
		Assert.assertEquals(true, client.isServiceEnabled("session-1"));
		client.detach();
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions?serviceName=notExistingService<br>
	 * (unknown service name)<br>
	 * Expectation: throws exception with exitCode = 4 "Unknown service"<br>
	 */
	@Test
	public void t54_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_SESSIONS + "?serviceName=notExistingService" });
		} catch (ExitException e) {
			Assert.assertEquals(4, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions?serviceName=session-1" parameters<br>
	 * Expectation: passes with exitCode = 0 "Success" <br>
	 */
	@Test
	public void t55_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_SESSIONS + "?serviceName=session-1" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions?serviceName=publish-1<br>
	 * Expectation: passes with exitCode = 0 "Success" <br>
	 */
	@Test
	public void t56_sessions_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_SESSIONS + "?serviceName=publish-1" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 sessions?serviceName=*<br>
	 * (unknown service name)<br>
	 * Expectation: passes with exitCode = 0 "Success"<br>
	 */
	@Test
	public void t57_sessions_command_wildCard() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_SESSIONS + "?serviceName=*" });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 dump<br>
	 * Expectation: passes with exitCode = 0 "Success".<br>
	 */
	@Test
	public void t60_dump_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_DUMP });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 clearCache<br>
	 * Expectation: passes with exitCode = 0 "Success".<br>
	 */
	@Test
	public void t70_clearCache() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_CLEAR_CACHE });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga?serviceName=notExistingService<br>
	 * (unknown command)<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command"<br>
	 */
	@Test
	public void t90_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					"gaga?serviceName=notExistingService" });
		} catch (ExitException e) {
			Assert.assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 gaga?serviceName=session-1<br>
	 * (unknown command)<br>
	 * Expectation: throws exception with exitCode = 3 "invalid command" <br>
	 */
	@Test
	public void t91_undefined_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					"gaga?serviceName=session-1" });
		} catch (ExitException e) {
			Assert.assertEquals(3, e.status);
		}
	}

	/**
	 * Description: start console with "-h localhost -p 9000 kill<br>
	 * Expectation: passes with exitCode = 0 "Success" <br>
	 * Post-condition: SC will be killed!
	 */
	@Test
	public void t99_kill_command() throws Exception {
		try {
			SCConsole.main(new String[] { "-h", TestConstants.HOST, "-p", String.valueOf(TestConstants.PORT_SC_TCP),
					Constants.CC_CMD_KILL });
		} catch (ExitException e) {
			Assert.assertEquals(0, e.status);
		}
	}
}
