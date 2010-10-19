/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.service;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * @author JTraber
 */
public class ServerLoader {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServerLoader.class);

	/**
	 * Loads remote hosts from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void load(String fileName) throws Exception {
		CompositeConfiguration config = new CompositeConfiguration();
		try {
			config.addConfiguration(new PropertiesConfiguration(fileName));
		} catch (Exception e) {
			throw new InvalidParameterException("could not find property file : " + fileName);
		}
		@SuppressWarnings("unchecked")
		List<String> remoteHostNames = config.getList(Constants.REMOTE_HOST);

		for (String remoteHostName : remoteHostNames) {
			// remove blanks in remoteHostName
			remoteHostName = remoteHostName.trim();

			int portNr = Integer.parseInt((String) config.getString(remoteHostName + Constants.PORT_QUALIFIER));
			String host = (String) config.getString(remoteHostName + Constants.HOST_QUALIFIER);
			String connectionType = (String) config.getString(remoteHostName + Constants.CONNECTION_TYPE_QUALIFIER);

			if (connectionType == null) {
				connectionType = Constants.DEFAULT_SERVER_CON;
			}
			String maxConnectionsValue = (String) config.getString(remoteHostName + Constants.MAX_CONNECTION_POOL_SIZE);
			int maxConnections = Constants.DEFAULT_MAX_CONNECTIONS;
			if (maxConnectionsValue != null) {
				maxConnections = Integer.parseInt(maxConnectionsValue);
			}

			String keepAliveIntervalValue = (String) config.getString(remoteHostName + Constants.KEEP_ALIVE_INTERVAL);
			int keepAliveInterval = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
			if (keepAliveIntervalValue != null) {
				keepAliveInterval = Integer.parseInt(keepAliveIntervalValue);
			}
			
//			ServiceType serviceType = ServiceType.getServiceType(serviceTypeString);
//			
//			InetSocketAddress socketAddress = new InetSocketAddress(host, portNr);			
//			Server server = new Server(socketAddress, null, portNr, maxConnections, keepAliveInterval);
//			// instantiate right type of server
//			
//			Service service = null;
//			switch (serviceType) {
//			case SESSION_SERVICE:
//				service = new SessionService(serviceName);
//				break;
//			case PUBLISH_SERVICE:
//				service = new PublishService(serviceName);
//				break;
//			case FILE_SERVICE:
//				service = new FileService(serviceName);
//				break;
//			case UNDEFINED:
//			default:
//				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
//						"wrong serviceType, serviceName/serviceType: " + serviceName + "/" + serviceTypeString);
//			}
		}
	}
}
