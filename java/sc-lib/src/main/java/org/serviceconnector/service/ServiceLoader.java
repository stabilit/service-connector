/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;

/**
 * @author JTraber
 */
public class ServiceLoader {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceLoader.class);

	/**
	 * Loads services from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void load(CompositeConfiguration config) throws Exception {
		@SuppressWarnings("unchecked")
		List<String> serviceNames = config.getList(Constants.PROPERTY_SERVICE_NAMES);

		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();

		for (String serviceName : serviceNames) {
			// remove blanks in serviceName
			serviceName = serviceName.trim();
			String serviceTypeString = (String) config.getString(serviceName + Constants.PROPERTY_QUALIFIER_TYPE);
			ServiceType serviceType = ServiceType.getServiceType(serviceTypeString);

			// instantiate right type of service
			Service service = null;
			switch (serviceType) {
			case SESSION_SERVICE:
				service = new SessionService(serviceName);
				break;
			case PUBLISH_SERVICE:
				service = new PublishService(serviceName);
				break;
			case FILE_SERVICE:
				String path = (String) config.getString(serviceName + Constants.PROPERTY_QUALIFIER_PATH);
				String scUploadFileScriptName = config.getString(serviceName + Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT);
				if (scUploadFileScriptName == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property "
							+ Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT + " is missing for service " + serviceName);
				}
				String scGetFileListScriptName = config.getString(serviceName + Constants.PROPERTY_QUALIFIER_LIST_SCRIPT);
				if (scGetFileListScriptName == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property "
							+ Constants.PROPERTY_QUALIFIER_LIST_SCRIPT + " is missing for service " + serviceName);
				}
				service = new FileService(serviceName, path, scUploadFileScriptName, scGetFileListScriptName);
				String remoteHost = (String) config.getString(serviceName + Constants.PROPERTY_QUALIFIER_REMOTE_HOST);
				Server server = AppContext.getServerRegistry().getServer(remoteHost);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host "+ remoteHost
							+ " configured for service " + serviceName + "does not exist");
				}
				((FileService) service).setServer((FileServer) server);
				break;
			case UNDEFINED:
			default:
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
						"wrong serviceType, serviceName/serviceType: " + serviceName + "/" + serviceTypeString);
			}

			// set service state as defined in configuration. Default is enabled
			String enable = config.getString(serviceName + Constants.PROPERTY_QUALIFIER_ENABLED);
			if (enable == null || enable.equals("true")) {
				service.setState(ServiceState.ENABLED); // default is enabled
				logger.trace("state enabled for service: " + serviceName);

			} else {
				service.setState(ServiceState.DISABLED);
				logger.trace("state disabled for service: " + serviceName);
			}
			serviceRegistry.addService(service.getServiceName(), service);
		}
	}
}
