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

import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.conf.ServiceConfiguration;
import org.serviceconnector.conf.ServiceListConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;

/**
 * @author JTraber
 */
public final class ServiceLoader {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ServiceLoader.class);

	/**
	 * Instantiates a new service loader.
	 */
	private ServiceLoader() {
	}

	/**
	 * Loads services from a file.
	 * 
	 * @param serviceListConfiguration
	 *            the service list configuration
	 * @throws Exception
	 *             the exception
	 */
	public static void load(ServiceListConfiguration serviceListConfiguration) throws Exception {
		Map<String, ServiceConfiguration> serviceConfigurationMap = serviceListConfiguration.getServiceConfigurations();
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();

		for (ServiceConfiguration serviceConfiguration : serviceConfigurationMap.values()) {
			String serviceTypeString = serviceConfiguration.getType();
			ServiceType serviceType = ServiceType.getType(serviceTypeString);
			RemoteNodeConfiguration remoteNode = serviceConfiguration.getRemoteNodeConfiguration();
			String remotNodeName = null;
			if (remoteNode != null) {
				remotNodeName = remoteNode.getName();
				serviceType = ServiceConfiguration.adaptServiceTypeIfCascService(serviceType, remotNodeName);
			}
			String serviceName = serviceConfiguration.getName();

			// instantiate right type of service
			Service service = null;
			switch (serviceType) {
			case CASCADED_SESSION_SERVICE:
				Server server = AppContext.getServerRegistry().getServer(remotNodeName);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remotNodeName
							+ " configured for service=" + serviceName + " is not configured");
				}
				service = new CascadedSessionService(serviceName, (CascadedSC) server);
				break;
			case CASCADED_PUBLISH_SERVICE:
				server = AppContext.getServerRegistry().getServer(remotNodeName);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remotNodeName
							+ " configured for service=" + serviceName + " is not configured");
				}

				service = new CascadedPublishService(serviceName, (CascadedSC) server, serviceConfiguration
						.getNoDataIntervalSeconds());
				break;
			case CASCADED_FILE_SERVICE:
				server = AppContext.getServerRegistry().getServer(remotNodeName);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remotNodeName
							+ " configured for service=" + serviceName + " is not configured");
				}
				service = new CascadedFileService(serviceName, (CascadedSC) server);
				break;
			case SESSION_SERVICE:
				service = new SessionService(serviceName);
				break;
			case PUBLISH_SERVICE:
				service = new PublishService(serviceName);
				break;
			case FILE_SERVICE:
				server = AppContext.getServerRegistry().getServer(remotNodeName);
				if (server == null) {
					throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, " host=" + remotNodeName
							+ " configured for service=" + serviceName + " is not configured");
				}
				service = new FileService(serviceName, (FileServer) server, serviceConfiguration.getPath(), serviceConfiguration
						.getUploadScript(), serviceConfiguration.getListScript());
				break;
			case UNDEFINED:
			default:
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
						"wrong serviceType, serviceName/serviceType=" + serviceName + "/" + serviceTypeString);
			}
			service.setEnabled(serviceConfiguration.getEnabled());
			serviceRegistry.addService(service.getName(), service);
		}
	}
}
