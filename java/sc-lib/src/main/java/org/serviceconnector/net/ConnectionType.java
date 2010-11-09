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
package org.serviceconnector.net;

public final class ConnectionType {

	private ConnectionType() {
		// instantiating not allowed
	}

	/** The Constant NETTY_TCP. */
	public static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	public static final String NETTY_HTTP = "netty.http";
	/** The Constant NETTY_WEB. */
	public static final String NETTY_WEB = "netty.web";
	/** The Constant NETTY_PROXY_HTTP. */
	public static final String NETTY_PROXY_HTTP = "netty-proxy.http";
	
	/** The DEFAULT_CLIENT_CONNECTION_TYPE. */
	public static final String DEFAULT_CLIENT_CONNECTION_TYPE = NETTY_TCP;
	/** The DEFAULT_SERVER_CONNECTION_TYPE. */
	public static final String DEFAULT_SERVER_CONNECTION_TYPE = NETTY_TCP;
}
