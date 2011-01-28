/*
 * -----------------------------------------------------------------------------*
 * *
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
 * -----------------------------------------------------------------------------*
 * /*
 * /**
 */
package org.serviceconnector.service;

import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;

/**
 * @author JTraber
 */
public class ServiceLoader {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ServiceLoader.class);

	/**
	 * Loads services from a file.
	 * 
	 * @param config
	 *            the config
	 * @throws Exception
	 *             the exception
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

			String remoteHost = (String) config.getString(serviceName + Constants.PROPERTY_QUALIFIER_REMOTE_HOST, null);
			// instantiate right type of service
			Service service = null;

			serviceType = ServiceLoader.adaptServiceTypeIfCascService(serviceType, remoteHost);

			switch (serviceType) {
			case CASCADED_SESSION_SERVICE:
				Server server = AppContext.getServerRegistry().getServer(remoteHost);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remoteHost
							+ " configured for service=" + serviceName + " is not configured");
				}
				service = new CascadedSessionService(serviceName, (CascadedSC) server);
				break;
			case CASCADED_PUBLISH_SERVICE:
				server = AppContext.getServerRegistry().getServer(remoteHost);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remoteHost
							+ " configured for service=" + serviceName + " is not configured");
				}
				int noDataIntervalInSeconds = config.getInt(serviceName + Constants.PROPERTY_QUALIFIER_NOI);
				service = new CascadedPublishService(serviceName, (CascadedSC) server, noDataIntervalInSeconds);
				break;
			case CASCADED_FILE_SERVICE:
				// TODO JOT
				continue;
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
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
							+ Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT + " is missing for service=" + serviceName);
				}
				String scGetFileListScriptName = config.getString(serviceName + Constants.PROPERTY_QUALIFIER_LIST_SCRIPT);
				if (scGetFileListScriptName == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property="
							+ Constants.PROPERTY_QUALIFIER_LIST_SCRIPT + " is missing for service=" + serviceName);
				}
				server = AppContext.getServerRegistry().getServer(remoteHost);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remoteHost
							+ " configured for service=" + serviceName + " is not configured");
				}
				service = new FileService(serviceName, (FileServer) server, path, scUploadFileScriptName, scGetFileListScriptName);
				break;
			case UNDEFINED:
			default:
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
						"wrong serviceType, serviceName/serviceType=" + serviceName + "/" + serviceTypeString);
			}

			// set service state as defined in configuration. Default is enabled
			String enable = config.getString(serviceName + Constants.PROPERTY_QUALIFIER_ENABLED);
			if (enable == null || enable.equals("true")) {
				service.setState(ServiceState.ENABLED); // default is enabled
				logger.trace("state enabled for service=" + serviceName);
			} else {
				service.setState(ServiceState.DISABLED);
				logger.trace("state disabled for service=" + serviceName);
			}
			serviceRegistry.addService(service.getName(), service);
		}
	}

	/**
	 * Adapt service type if cascaded service. SC uses more service type internal. This method figures out if changing of service
	 * type is necessary for current service.
	 * 
	 * @param serviceType
	 *            the service type
	 * @param remoteHost
	 *            the remote host
	 * @return the service type
	 */
	private static ServiceType adaptServiceTypeIfCascService(ServiceType serviceType, String remoteHost) {
		switch (serviceType) {
		case SESSION_SERVICE:
			if (remoteHost != null) {
				return ServiceType.CASCADED_SESSION_SERVICE;
			}
		case PUBLISH_SERVICE:
			if (remoteHost != null) {
				return ServiceType.CASCADED_PUBLISH_SERVICE;
			}
		case FILE_SERVICE:
			if (remoteHost != null) {
				return ServiceType.CASCADED_FILE_SERVICE;
			}
		}
		return serviceType;
	}
}
