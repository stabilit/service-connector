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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.net.res.netty.http.NettyHttpEndpoint;
import org.serviceconnector.net.res.netty.tcp.NettyTcpEndpoint;
import org.serviceconnector.net.res.netty.tcp.proxy.NettyTcpProxyEndpoint;
import org.serviceconnector.net.res.netty.web.NettyWebEndpoint;

/**
 * A factory for creating Endpoint objects. Provides access to concrete endpoint instances. Possible endpoints are shown
 * in key string constants below.
 */
public class EndpointFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EndpointFactory.class);

	public IEndpoint createEndpoint(String key) {
		if (Constants.NETTY_HTTP.equalsIgnoreCase(key)) {
			return new NettyHttpEndpoint();
		} else if (Constants.NETTY_TCP.equalsIgnoreCase(key)) {
			return new NettyTcpEndpoint();
		} else if (Constants.NETTY_WEB.equalsIgnoreCase(key)) {
			return new NettyWebEndpoint();
		} else if (Constants.NETTY_PROXY_HTTP.equalsIgnoreCase(key)) {
			return new NettyTcpProxyEndpoint();
		} else {
			logger.fatal("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
	}
}
