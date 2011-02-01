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

import org.apache.commons.configuration.CompositeConfiguration;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.server.ServerType;
import org.serviceconnector.service.ServiceType;

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
	/** The remote node configuration for file services and cascased services. */
	private RemoteNodeConfiguration remoteNodeConfiguration;

	/**
	 * The Constructor.
	 * 
	 * @param name
	 *            the node name
	 */
	public ServiceConfiguration(String name) {
		this.name = name;
		this.type = null;
		this.enabled = Constants.DEFAULT_SERVICE_ENABLED;
		this.path = null;
		this.uploadScript = null;
		this.listScript = null;
		this.remoteNodeConfiguration = null;
	}
	
	/**
	 * Load the configured items
	 * 
	 * @param compositeConfig
	 * @throws SCMPValidatorException
	 */
	public void load(CompositeConfiguration compositeConfig) throws SCMPValidatorException {
		
		// get type
		this.type = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_TYPE);
		if (type == null) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_TYPE + " is missing");
		}
		ServiceType serviceType = ServiceType.getType(this.type);
		if (serviceType == ServiceType.UNDEFINED) {
			throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "unkown serviceType=" + this.name
					+ this.type);
		}

		// get enabled
		this.enabled = compositeConfig.getBoolean(this.name + Constants.PROPERTY_QUALIFIER_ENABLED, Constants.DEFAULT_SERVICE_ENABLED);

		// get path & uploadScript & listScript for file service
		if (serviceType == ServiceType.FILE_SERVICE) {
			this.path = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_PATH, null);
			if (this.path == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
						+ Constants.PROPERTY_QUALIFIER_PATH + " is missing");	
			}

			this.uploadScript = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT, null);
			if (this.uploadScript == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
						+ Constants.PROPERTY_QUALIFIER_UPLOAD_SCRIPT + " is missing");	
			}
			this.listScript = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_LIST_SCRIPT, null);
			if (this.listScript == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
						+ Constants.PROPERTY_QUALIFIER_LIST_SCRIPT + " is missing");
			}
		}
		
		// get remote host for file services or cascaded services
		if ((serviceType == ServiceType.FILE_SERVICE) || 
			(serviceType == ServiceType.CASCADED_SESSION_SERVICE) || 
			(serviceType == ServiceType.CASCADED_PUBLISH_SERVICE) ||
			(serviceType == ServiceType.CASCADED_FILE_SERVICE)) {
			String remoteNode = compositeConfig.getString(this.name + Constants.PROPERTY_QUALIFIER_REMOTE_NODE);
			if (remoteNode == null) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, "required property=" + this.name
					+ Constants.PROPERTY_QUALIFIER_REMOTE_NODE + " is missing");
			}
			// create configuration for remote host
			RemoteNodeConfiguration remoteNodeConfig = new RemoteNodeConfiguration(remoteNode);
			// load it with the configurated items
			remoteNodeConfig.load(compositeConfig);
			// remote node must be a web server
			if (remoteNodeConfig.getServerType().equals(ServerType.WEB_SERVER.getValue())) {
				throw new SCMPValidatorException(SCMPError.V_WRONG_CONFIGURATION_FILE, this.name
					+ Constants.PROPERTY_QUALIFIER_REMOTE_NODE + " is not a web server");
			}
			// set remote host configuration into the listener configuration
			this.remoteNodeConfiguration = remoteNodeConfig;
		}
	
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.name);
		builder.append(" type=");
		builder.append(this.type);
		builder.append(" [");
		builder.append(this.enabled);
		builder.append(" ] ");
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
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getPath() {
		return path;
	}

	public String getUploadScript() {
		return uploadScript;
	}

	public String getListScript() {
		return listScript;
	}

	public RemoteNodeConfiguration getRemoteNodeConfiguration() {
		return remoteNodeConfiguration;
	}
	
}
