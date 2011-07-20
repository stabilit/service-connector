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
package org.serviceconnector.web;

/**
 * The class WebConstants. SC web constants.
 * 
 * @author JTraber
 */
public final class WebConstants {

	/**
	 * Instantiates a new constants.
	 */
	private WebConstants() {
		// instantiating not allowed
	}

	/** The Constant PROPERTY_SERVICE_NAME. */
	public static final String PROPERTY_SERVICE_NAME = "serviceName";
	/** The Constant PROPERTY_MAX_SESSIONS. */
	public static final String PROPERTY_MAX_SESSIONS = "maxSessions";
	/** The Constant PROPERTY_MAX_CONNECTIONS. */
	public static final String PROPERTY_MAX_CONNECTIONS = "maxConnections";
	/** The Constant PROPERTY_SC_HOST. */
	public static final String PROPERTY_SC_HOST = "scHost";
	/** The Constant PROPERTY_SC_PORT. */
	public static final String PROPERTY_SC_PORT = "scPort";
	/** The Constant PROPERTY_KEEPALIVE_TOSC. */
	public static final String PROPERTY_KEEPALIVE_INTERVAL_TOSC = "toSCKeepAliveIntervalSeconds";
	/** The Constant PROPERTY_CHECK_REGISTRATIONO_INTERVAL. */
	public static final String PROPERTY_CHECK_REGRISTRATION_INTERVAL = "checkRegistrationIntervalSeconds";
	/** The Constant PROPERTY_KEEPALIVE_OTI. */
	public static final String PROPERTY_KEEPALIVE_OTI = "keepAliveTimeoutSeconds";
	/** The Constant PROPERTY_TOMCAT_PORT. */
	public static final String PROPERTY_TOMCAT_PORT = "tomcatPort";
}
