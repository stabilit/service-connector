/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package com.stabilit.sc.console;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.util.CommandLineUtil;

public class SCConsole {

	public static void main(String[] args) throws Exception {
		System.out.println("Stabilit Service Connector Console\n");
		// check arguments
		if (args.length <= 0) {
			showError("no argumments");
			System.exit(1);
		}
		String host = CommandLineUtil.getArg(args, Constants.CLI_HOST_ARG);
		if (host == null) {
			showError("Host argument is missing");
			System.exit(1);
		}
		String port = CommandLineUtil.getArg(args, Constants.CLI_PORT_ARG);
		if (port == null) {
			showError("Port argument is missing");
			System.exit(1);
		}
		ConsoleCommand consoleCommand = ConsoleCommand.UNDEFINED;
		String commandKey = "";
		String commandValue = "";
		for (int i = 0; i < args.length; i++) {
			String[] splitted = args[i].split("=");
			if (splitted.length != 2) {
				continue;
			}
			commandKey = splitted[0];
			commandValue = splitted[1];
			consoleCommand = ConsoleCommand.getCommand(commandKey);
			if (consoleCommand != ConsoleCommand.UNDEFINED) {
				break;
			}
		}
		if (consoleCommand == ConsoleCommand.UNDEFINED) {
			showError("invalid or no command (enable|disable|state|sessions)");
			System.exit(3);
		}
		// fileName extracted from vm arguments
		SCConsole.run(host, port, consoleCommand, commandValue);
		System.exit(0);
	}

	/**
	 * Run SCConsole command
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run(String host, String port, ConsoleCommand cmd, String commandValue) throws Exception {

		try {
			int portNr = Integer.parseInt(port);
			SCClient client = new SCClient();
			client.attach(host, portNr);
			switch (cmd) {
			case DISABLE:
				client.disableService(commandValue);
				System.out.println("Service [" + commandValue + "] has been disabled");
				break;
			case ENABLE:
				client.enableService(commandValue);
				System.out.println("Service [" + commandValue + "] has been enabled");
				break;
			case STATE:
				try {
					boolean enabled = client.isServiceEnabled(commandValue);
					if (enabled) {
						System.out.println("Service [" + commandValue + "] is enabled");
					} else {
						System.out.println("Service [" + commandValue + "] is disabled");
					}
				} catch (Exception e) {
					System.out.println("Serivce [" + commandValue + "] does not exist!");
				}
				break;
			case SESSIONS:
				try {
					String sessions = client.workload(commandValue);
					System.out.println("Service [" + commandValue + "] has " + sessions + " Sessions");
				} catch (Exception e) {
					System.out.println("Serivce [" + commandValue + "] does not exist!");
				}
				break;
			}
			client.detach();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void showError(String msg) {
		System.err.println("error: " + msg);
		System.out.println("\nusage  : java -jar scconsole.jar -h <host> -p <port> <enable|disable|state|sessions=service>");
		System.out.println("\nsamples: java -jar scconsole.jar -h localhost -p 8000 enable=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 8000 disable=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 8000 state=abc");
		System.out.println("         java -jar scconsole.jar -h localhost -p 8000 sessions=abc");
	}
}
