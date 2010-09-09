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
package com.stabilit.scm.console;

import java.util.List;

import com.stabilit.scm.cln.SCClient;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.util.ConsoleUtil;

public class SCConsole {

	public static void main(String[] args) throws Exception {
		System.out.println("Stabilit Service Connector Console\n");
		// check arguments
		if (args.length <= 0) {
			showError("no args");
			System.exit(1);
		}
		String fileName = ConsoleUtil.getArg(args, "-filename");
		SCConsoleCommand consoleCommand = SCConsoleCommand.UNDEFINED;
		String commandKey = "";
		String commandValue = "";
		for (int i = 0; i < args.length; i++) {
			String[] splitted = args[i].split("=");
			if (splitted.length != 2) {
				continue;
			}
			commandKey = splitted[0];
			commandValue = splitted[1];
			consoleCommand = SCConsoleCommand.getCommand(commandKey);
			if (consoleCommand != SCConsoleCommand.UNDEFINED) {
				break;
			}
		}
		if (consoleCommand == SCConsoleCommand.UNDEFINED) {
			showError("invalid or no command (enable|disable|show)");
			System.exit(3);
		}			
		if (fileName == null) {
			// fileName not set - use default
			SCConsole.run(Constants.DEFAULT_PROPERTY_FILE_NAME, consoleCommand,
					commandValue);
		} else {
			// fileName extracted from vm arguments
			SCConsole.run(fileName, consoleCommand, commandValue);
		}
		System.out.println("\nCopyright © 2010 STABILIT Informatik AG, Switzerland");
		System.exit(0);
	}

	/**
	 * Run SCConsole command
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private static void run(String fileName, SCConsoleCommand cmd,
			String commandValue) throws Exception {
		ResponderConfigPool config = new ResponderConfigPool();
		config.load(fileName);
		List<ICommunicatorConfig> respConfigList = config
				.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			try {
				String host = respConfig.getHost();
				int port = respConfig.getPort();
				SCClient client = new SCClient();
				System.out.println("attach client");
				client.attach(host, port);
				switch (cmd) {
				case DISABLE:
					System.out.println("disable service " + commandValue + "...");
					client.disableService(commandValue);
					System.out.println("... service " + commandValue + " has been disabled");
					break;
				case ENABLE:
					System.out.println("enable service " + commandValue + "...");
					client.enableService(commandValue);
					System.out.println("... service " + commandValue + " has been enabled");
					break;
				case SHOW:
					System.out.println("show service " + commandValue + "...");
					try {
						boolean enabled = client.isServiceEnabled(commandValue);
						if (enabled) {
							System.out.println("Service [" + commandValue
									+ "] is enabled");
						} else {
							System.out.println("Service [" + commandValue
									+ "] is disabled");
						}
						// get workload
						String sessions = client.workload(commandValue);
						System.out.println("Service [" + commandValue
								+ "] has " + sessions + " Sessions");
					} catch (Exception e) {
						System.out.println("Serivce [" + commandValue + "] does not exist!");
						//e.printStackTrace();
					}
					break;
				}
				System.out.println("detach client");
				client.detach();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void showError(String msg) {
		System.err.println("error: " + msg);
		System.out.println("\nusage  : java -jar scconsole.jar -filename <properties file> <enable|disable|show=service>");
		System.out.println("\nsamples: java -jar scconsole.jar -filename sc.properties enable=abc");
		System.out.println("         java -jar scconsole.jar disable=abc");
		System.out.println("         java -jar scconsole.jar show=abc");
	}
}
