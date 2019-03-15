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
package org.serviceconnector.conf;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.ServerType;
import org.serviceconnector.service.ServiceType;

/**
 * The Class ServiceConfiguration.
 */
public class ServiceConfiguration {

	/** The name. */
	private String name;
	/** The type. */
	private String type;
	/** The enabled. */
	private Boolean enabled;
	/** The path for file service. */
	private String path;
	/** The uploadScript for file service. */
	private String uploadScript;
	/** The listScript for file service. */
	private String listScript;
	/** The nod data interval in seconds. */
	private int nodDataIntervalSeconds;
	/** The remote node configuration for file services and cascased services. */
	private RemoteNodeConfiguration remoteNodeConfiguration;

	/**
	 * The Constructor.
	 *
	 * @param name the node name
	 */
	public ServiceConfiguration(String name) {
		this.name = name;
		this.type = null;
		this.enabled = Constants.DEFAULT_SERVICE_ENABLED;
		this.path = null;
		this.uploadScript = null;
		this.listScript = null;
		this.remoteNodeConfiguration = null;
		this.nodDataIntervalSeconds = Constants.DEFAULT_NO_DATA_INTERVAL_SECONDS;
	}

	/**
	 * Load the configured items.
	 *
	 * @param compositeConfig the composite config
	 * @throws SCMPValidatorException the sCMP validator exception
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {

		// get type
		this.type = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_TYPE);
		if (type == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_TYPE + " is missing");
		}
		ServiceType serviceType = ServiceType.getType(this.type);
		if (serviceType == ServiceType.UNDEFINED) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown serviceType=" + this.name + this.type);
		}
		String remoteNode = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE);
		if (remoteNode != null) {
			RemoteNodeConfiguration remoteNodeConfigurationLocal = AppContext.getRequesterConfiguration().getRequesterConfigurations().get(remoteNode);
			if (remoteNodeConfigurationLocal == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown remoteNode=" + remoteNode);
			}
			serviceType = ServiceConfiguration.adaptServiceTypeIfCascService(serviceType, remoteNode);
		}

		// get enabled
		this.enabled = compositeConfig.getBoolean(this.name + Constants.PROPERTY_QUALIFIER_ENABLED, Constants.DEFAULT_SERVICE_ENABLED);

		// get path & uploadScript & listScript for file service
		if (serviceType == ServiceType.FILE_SERVICE) {
			this.path = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_PATH, null);
			if (this.path == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_PATH + " is missing");
			}
			if (this.path.endsWith("/") == false) {
				// adds a slash to the path
				this.path += "/";
			}
			this.uploadScript = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT, null);
			if (this.uploadScript == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE,
						"required property=" + this.name + Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT + " is missing");
			}
			this.listScript = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_LIST_SCRIPT, null);
			if (this.listScript == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_LIST_SCRIPT + " is missing");
			}
		}

		// get remote host for file services or cascaded services
		if ((serviceType == ServiceType.FILE_SERVICE) || (serviceType == ServiceType.CASCADED_SESSION_SERVICE) || (serviceType == ServiceType.CASCADED_PUBLISH_SERVICE)
				|| (serviceType == ServiceType.CASCADED_FILE_SERVICE) || (serviceType == ServiceType.CASCADED_CACHE_GUARDIAN)) {
			if (remoteNode == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE + " is missing");
			}
			// create configuration for remote host
			RemoteNodeConfiguration remoteNodeConfig = new RemoteNodeConfiguration(remoteNode);
			// load it with the configurated items
			remoteNodeConfig.load(compositeConfig);
			// remote node must be a web server
			if (remoteNodeConfig.getServerType().equals(ServerType.WEB_SERVER.getValue())) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE + " is not a web server");
			}
			// set remote host configuration into the listener configuration
			this.remoteNodeConfiguration = remoteNodeConfig;
		}

		if ((serviceType == ServiceType.CASCADED_PUBLISH_SERVICE) || (serviceType == ServiceType.CASCADED_CACHE_GUARDIAN)) {
			Integer noDataIntervalSecondsInteger = compositeConfig.getInteger(this.name + Constants.PROPERTY_QUALIFIER_NOI, null);
			if (noDataIntervalSecondsInteger == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name + Constants.PROPERTY_QUALIFIER_NOI + " is missing");
			}
			this.nodDataIntervalSeconds = noDataIntervalSecondsInteger;
		}

	}

	/**
	 * To string.
	 *
	 * @return the string {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append(" type=");
		builder.append(this.type);
		if (this.enabled) {
			builder.append(" [enabled] ");
		} else {
			builder.append(" [disabled] ");
		}
		if (this.remoteNodeConfiguration != null) {
			builder.append("/remote=");
			builder.append(this.remoteNodeConfiguration.getHost());
		}
		if (this.path != null) {
			builder.append("/path=");
			builder.append(this.path);
			builder.append("/uload=");
			builder.append(this.uploadScript);
			builder.append("/list=");
			builder.append(this.listScript);
		}
		return builder.toString();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the enabled.
	 *
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets the upload script.
	 *
	 * @return the upload script
	 */
	public String getUploadScript() {
		return uploadScript;
	}

	/**
	 * Gets the list script.
	 *
	 * @return the list script
	 */
	public String getListScript() {
		return listScript;
	}

	/**
	 * Gets the remote node configuration.
	 *
	 * @return the remote node configuration
	 */
	public RemoteNodeConfiguration getRemoteNodeConfiguration() {
		return remoteNodeConfiguration;
	}

	/**
	 * Gets the no data interval seconds.
	 *
	 * @return the no data interval seconds
	 */
	public int getNoDataIntervalSeconds() {
		return this.nodDataIntervalSeconds;
	}

	/**
	 * Adapt service type if cascaded service. SC uses more service type internal. This method figures out if changing of service type is necessary for current service.
	 *
	 * @param serviceType the service type
	 * @param remoteHost the remote host
	 * @return the service type
	 */
	public static ServiceType adaptServiceTypeIfCascService(ServiceType serviceType, String remoteHost) {
		switch (serviceType) {
			case SESSION_SERVICE:
				if (remoteHost != null) {
					return ServiceType.CASCADED_SESSION_SERVICE;
				}
			case PUBLISH_SERVICE:
				if (remoteHost != null) {
					return ServiceType.CASCADED_PUBLISH_SERVICE;
				}
			case CACHE_GUARDIAN:
				if (remoteHost != null) {
					return ServiceType.CASCADED_CACHE_GUARDIAN;
				}
			case FILE_SERVICE:
				if (remoteHost != null) {
					RemoteNodeConfiguration remoteNodeConfiguration = AppContext.getRequesterConfiguration().getRequesterConfigurations().get(remoteHost);
					if (remoteNodeConfiguration.getServerType() == ServerType.CASCADED_SC) {
						return ServiceType.CASCADED_FILE_SERVICE;
					}
					return serviceType;
				}
			default:
				break;
		}
		return serviceType;
	}
}
