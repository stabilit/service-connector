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
package com.stabilit.scm.common.net.res;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.net.res.netty.http.NettyHttpEndpoint;
import com.stabilit.scm.common.net.res.netty.tcp.NettyTcpEnpoint;
import com.stabilit.scm.common.res.IEndpoint;

/**
 * A factory for creating Endpoint objects. Provides access to concrete endpoint instances. Possible endpoints are shown
 * in key string constants below.
 */
public class EndpointFactory extends Factory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EndpointFactory.class);
	
	/** EndpointFactory instance */
	private static final EndpointFactory instance = new EndpointFactory();
	/** The Constant NETTY_TCP. */
	private static final String NETTY_TCP = "netty.tcp";
	/** The Constant NETTY_HTTP. */
	private static final String NETTY_HTTP = "netty.http";

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static EndpointFactory getCurrentInstance() {
		return EndpointFactory.instance;
	}

	/**
	 * Instantiates a new EnpointFactory.
	 */
	private EndpointFactory() {
		// jboss netty http endpoint
		IEndpoint nettyHttpEndpoint = new NettyHttpEndpoint();
		add(DEFAULT, nettyHttpEndpoint);
		add(NETTY_HTTP, nettyHttpEndpoint);
		// jboss netty tcp endpoint
		IEndpoint nettyTCPEndpoint = new NettyTcpEnpoint();
		add(NETTY_TCP, nettyTCPEndpoint);
	}

	/** {@inheritDoc} */
	public IEndpoint newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return the endpoint
	 */
	public IEndpoint newInstance(String key) {
		IFactoryable factoryInstance = super.newInstance(key);
		return (IEndpoint) factoryInstance; // should be a clone if implemented
	}
}
