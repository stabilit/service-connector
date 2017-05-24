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
package org.serviceconnector.net.res;

import java.security.InvalidParameterException;

import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.res.netty.http.NettyHttpEndpoint;
import org.serviceconnector.net.res.netty.tcp.NettyTcpEndpoint;
import org.serviceconnector.net.res.netty.tcp.proxy.NettyTcpProxyEndpoint;
import org.serviceconnector.net.res.netty.web.NettyWebEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating Endpoint objects. Provides access to concrete endpoint instances. Possible endpoints are shown in key string constants below.
 */
public class EndpointFactory {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(EndpointFactory.class);

	/**
	 * Creates a new Endpoint object.
	 *
	 * @param key the key
	 * @return the i endpoint
	 */
	public IEndpoint createEndpoint(String key) {
		if (ConnectionType.NETTY_HTTP.getValue().equalsIgnoreCase(key)) {
			return new NettyHttpEndpoint();
		} else if (ConnectionType.NETTY_TCP.getValue().equalsIgnoreCase(key)) {
			return new NettyTcpEndpoint();
		} else if (ConnectionType.NETTY_WEB.getValue().equalsIgnoreCase(key)) {
			return new NettyWebEndpoint();
		} else if (ConnectionType.NETTY_PROXY_HTTP.getValue().equalsIgnoreCase(key)) {
			return new NettyTcpProxyEndpoint();
		} else {
			LOGGER.error("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
	}
}
