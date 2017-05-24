/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.console;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.ListenerListConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.CommandLineUtil;
import org.serviceconnector.util.URLString;
import org.serviceconnector.util.ValidatorUtility;

public class SCConsole {

	/**
	 * @param args usage : java -jar sc-console.jar -h <host> -p <port> <<<enable|disable|state|sessions>?serviceName=[serviceName]>|clearCache|dump|kill>"); java -jar
	 *        sc-console.jar -h localhost -p 7000 enable?serviceName=abc java -jar sc-console.jar -h localhost -p 7000 disable?serviceName=abc java -jar sc-console.jar -h localhost
	 *        -p 7000 state?serviceName=abc java -jar sc-console.jar -h localhost -p 7000 sessions?serviceName=abc java -jar sc-console.jar -h localhost -p 7000 clearCache java
	 *        -jar sc-console.jar -h localhost -p 7000 dump java -jar sc-console.jar -h localhost -p 7000 kill java -jar sc-console.jar -h localhost -p 7000 scVersion java -jar
	 *        sc-console.jar -h localhost -p 7000 serviceConfiguration?serviceName=abc java -jar sc-console.jar -config sc.properties kill system exit status<br />
	 *        0 = success 1 = error parsing arguments 3 = invalid command 4 = service not found 5 = unexpected error
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// check arguments
		if (args == null || args.length <= 0) {
			showError("no argumments");
			System.exit(1);
		} else if (args.length < 3) {
			showError("not enough argumments");
			System.exit(1);
		} else if (args.length > 5) {
			showError("too many argumments");
			System.exit(1);
		}

		// check config
		String configFileName = CommandLineUtil.getArg(args, ConsoleConstants.CLI_CONFIG_ARG);
		if (configFileName != null) {
			// get command from args[2]
			String bodyString = args[2];
			if (bodyString == null) {
				showError("Command is missing");
				System.exit(1);
			}
			int status = SCConsole.run(configFileName, bodyString);
			System.exit(status);
		}

		if (args.length < 5) {
			// configFileName is null and number of arguments lower than 3
			showError("Config file name argument is missing");
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
		int status = SCConsole.run(host, Integer.parseInt(port), bodyString);
		System.exit(status);
	}

	/**
	 * Run SCConsole command.
	 *
	 * @param host the host
	 * @param port the port
	 * @param bodyString the body string
	 * @return the int - return status
	 * @throws Exception the exception
	 */
	private static int run(String host, int port, String bodyString) throws Exception {

		int status = 0;
		try {
			URLString urlRequestString = new URLString();
			urlRequestString.parseRequestURLString(bodyString);
			String callKey = urlRequestString.getCallKey();
			String serviceName = urlRequestString.getParamValue("serviceName");

			SCMgmtClient client = new SCMgmtClient(host, port, ConnectionType.NETTY_TCP);
			client.attach();

			if (callKey.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
				client.killSC();
				System.out.println("SC exit requested");
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_DUMP)) {
				client.dump();
				System.out.println("SC dump requested");
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_CLEAR_CACHE)) {
				client.clearCache();
				System.out.println("Cache has been cleared");
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_ENABLE)) {
				try {
					client.enableService(serviceName);
					System.out.println("Service [" + serviceName + "] has been enabled");
				} catch (SCServiceException e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_DISABLE)) {
				try {
					client.disableService(serviceName);
					System.out.println("Service [" + serviceName + "] has been disabled");
				} catch (SCServiceException e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_STATE)) {
				try {
					Map<String, String> stateMap = client.getStateOfServices(serviceName);
					Set<Entry<String, String>> parameters = stateMap.entrySet();
					StringBuilder sb = new StringBuilder();
					if (parameters.size() == 0) {
						System.out.println("Service [" + serviceName + "] does not exist!");
						status = 4;
					} else {
						for (Entry<String, String> param : parameters) {
							sb.append("Service [");
							sb.append(param.getKey());
							sb.append("] is ");
							sb.append(param.getValue());
							sb.append("\n");
						}
						if (sb.length() > 0) {
							System.out.println(sb.toString());
						}
					}
				} catch (SCServiceException e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_SESSIONS)) {
				try {
					Map<String, String> workloadMap = client.getWorkload(serviceName);
					Set<Entry<String, String>> workloads = workloadMap.entrySet();
					StringBuilder sb = new StringBuilder();
					for (Entry<String, String> param : workloads) {
						sb.append("Service [");
						sb.append(param.getKey());
						sb.append("] has ");
						sb.append(param.getValue());
						sb.append(" sessions\n");
					}
					System.out.println(sb.toString());
				} catch (SCServiceException e) {
					System.out.println("Service [" + serviceName + "] does not exist!");
					status = 4;
				}
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_SC_VERSION)) {
				try {
					String scVersion = client.getSCVersion();
					System.out.println(scVersion);
				} catch (SCServiceException e) {
					System.out.println("Getting version (SC Version) of remote SC failed.");
					status = 4;
				}
				client.detach();
			} else if (callKey.equalsIgnoreCase(Constants.CC_CMD_SERVICE_CONF)) {
				try {
					Map<String, String> serviceConf = client.getServiceConfiguration(serviceName);
					System.out.println(serviceConf);
				} catch (SCServiceException e) {
					System.out.println("Getting service [" + serviceName + "] configuration failed.");
					status = 4;
				}
				client.detach();
			} else {
				SCConsole.showError("Error - wrong call key in request string.");
				status = 3;
			}
		} catch (UnsupportedEncodingException e) {
			SCConsole.showError("Error in request string, parsing failed.");
			status = 5;
		} catch (Exception e) {
			e.printStackTrace();
			status = 5;
		}
		return status;
	}

	/**
	 * Run.
	 *
	 * @param configFileName the config file name
	 * @return the int - return status
	 * @throws Exception the exception
	 */
	private static int run(String configFileName, String bodyString) throws Exception {
		int status = 5;

		AppContext.initConfiguration(configFileName);
		AppContext.getBasicConfiguration().load(AppContext.getApacheCompositeConfig());
		AppContext.getRequesterConfiguration().load(AppContext.getApacheCompositeConfig());
		ListenerListConfiguration responderConfiguration = AppContext.getResponderConfiguration();
		responderConfiguration.load(AppContext.getApacheCompositeConfig(), AppContext.getRequesterConfiguration());

		for (ListenerConfiguration listenerConfiguration : responderConfiguration.getListenerConfigurations().values()) {
			String connectionTypeString = listenerConfiguration.getConnectionType();
			ConnectionType connectionType = ConnectionType.getType(connectionTypeString);

			switch (connectionType) {
				case DEFAULT_SERVER_CONNECTION_TYPE:
				case NETTY_TCP:
					return run(listenerConfiguration.getNetworkInterfaces().get(0), listenerConfiguration.getPort(), bodyString);
				default:
					continue;
			}
		}
		if (status == 5) {
			SCConsole.showError("No tcp listener is defined in the configuration file");
		}
		return status;
	}

	/**
	 * Show error.
	 *
	 * @param msg the msg
	 */
	private static void showError(String msg) {
		System.err.println("\nerror: " + msg);
		System.out.println("\nusage  : java -jar sc-console.jar -h <host> -p <port> <<<enable|disable|state|sessions>?serviceName=[serviceName]>|clearCache|dump|kill>");
		System.out.println("\nsamples: java -jar sc-console.jar -h localhost -p 7000 enable?serviceName=abc");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 disable?serviceName=abc");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 state?serviceName=abc");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 sessions?serviceName=abc");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 clearCache");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 dump");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 kill");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 scVersion");
		System.out.println("         java -jar sc-console.jar -h localhost -p 7000 serviceConfiguration?serviceName=abc");
		System.out.println("         java -jar sc-console.jar -config sc.properties kill");
	}
}
