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
package org.serviceconnector.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.serviceconnector.Constants;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.CommandLineUtil;
import org.serviceconnector.util.ValidatorUtility;

public class SCConsole {

	/**
	 * @param args
	 *            usage : java -jar scconsole.jar -h <host> -p <port> <<<enable|disable|state|sessions>=service>dump|clearCache|kill><br>
	 *            samples: java -jar scconsole.jar -h localhost -p 7000 enable=abc<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 disable=abc<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 state=abc<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 sessions=abc<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 clearCache<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 dump<br>
	 *            java -jar scconsole.jar -h localhost -p 7000 kill<br>
	 *            
	 * system exit status<br>
	 * 				0 = success
	 * 				1 = error parsing arguments
	 * 				3 = invalid command
	 * 				4 = service not found
	 * 				5 = unexpected error 
	 *            
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// check arguments
		if (args == null || args.length <= 0) {
			showError("no argumments");
			System.exit(1);
		} else if (args.length < 5) {
			showError("not enough argumments");
			System.exit(1);
		} else if (args.length > 5) {
			showError("too many argumments");
			System.exit(1);
		}

		// get command from args[4]
		String bodyString = args[4];
		if (bodyString == null) {
			showError("Command is missing");
			System.exit(1);
		}

		// check host
		String host = CommandLineUtil.getArg(args, ConsoleConstants.CLI_HOST_ARG);
		// check port
		String port = CommandLineUtil.getArg(args, ConsoleConstants.CLI_PORT_ARG);

		if (host == null) {
			showError("Host argument is missing");
			System.exit(1);
		}
		if (port == null) {
			showError("Port argument is missing");
			System.exit(1);
		} else {
			ValidatorUtility.validateInt(1, port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		}
		int status = SCConsole.run(host, port, bodyString);
		System.exit(status);
	}

	/**
	 * Run SCConsole command
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static int run(String arg0, String arg1, String bodyString) throws Exception {

		/** The Constant COMMAND_REGEX_STRING. */
		String regex = "(" + Constants.CC_CMD_KILL + "|" + Constants.CC_CMD_DUMP + "|" + Constants.CC_CMD_CLEAR_CACHE + "|("
				+ Constants.CC_CMD_ENABLE + "|" + Constants.CC_CMD_DISABLE + "|" + Constants.CC_CMD_STATE + "|" + Constants.CC_CMD_SESSIONS + ")"
				+ Constants.EQUAL_SIGN + "(.*))";
		int status = 0;

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(bodyString);
		if (!m.matches()) {
			showError("invalid or no command="+bodyString);
			return 3;
		}
		String command = m.group(1);
		String function = m.group(2);
		String serviceName = m.group(3);

		try {
			SCMgmtClient client = new SCMgmtClient(arg0, Integer.parseInt(arg1), ConnectionType.NETTY_TCP);
			client.attach();

			if (command.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
				client.killSC();
				System.out.println("SC exit requested");
			} else if (command.equalsIgnoreCase(Constants.CC_CMD_DUMP)) {
				client.dump();
				System.out.println("SC dump requested");
				client.detach();
			} else if (command.equalsIgnoreCase(Constants.CC_CMD_CLEAR_CACHE)) {
				client.clearCache();
				System.out.println("Cache has been cleared");
				client.detach();
			} else if (function.equalsIgnoreCase(Constants.CC_CMD_ENABLE)) {
				client.enableService(serviceName);
				System.out.println("Service [" + serviceName + "] has been enabled");
				client.detach();
			} else if (function.equalsIgnoreCase(Constants.CC_CMD_DISABLE)) {
				client.disableService(serviceName);
				System.out.println("Service [" + serviceName + "] has been disabled");
				client.detach();
			} else if (function.equalsIgnoreCase(Constants.CC_CMD_STATE)) {
				try {
					boolean enabled = client.isServiceEnabled(serviceName);
					if (enabled) {
						System.out.println("Service [" + serviceName + "] is enabled");
					} else {
						System.out.println("Service [" + serviceName + "] is disabled");
					}
				} catch (Exception e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			} else if (function.equalsIgnoreCase(Constants.CC_CMD_SESSIONS)) {
				try {
					String sessions = client.getWorkload(serviceName);
					System.out.println("Service [" + serviceName + "] has " + sessions + " sessions");
				} catch (Exception e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = 5;
		}
		return status;
	}

	private static void showError(String msg) {
		System.err.println("\nerror: " + msg);
		System.out.println("\nusage  : java -jar scconsole.jar -h <host> -p <port> <<<enable|disable|state|sessions>=service>clearCache|dump|kill>");
		System.out.println("\nsamples: java -jar scconsole.jar -h localhost -p 7000 enable=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 disable=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 state=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 sessions=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 clearCache");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 dump");
		System.out.println("         java -jar scconsole.jar -h localhost -p 7000 kill");
	}
}