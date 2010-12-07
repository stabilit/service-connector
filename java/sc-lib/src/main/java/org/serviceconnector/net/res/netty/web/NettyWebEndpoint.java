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
package org.serviceconnector.net.res.netty.web;

import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.serviceconnector.net.res.EndpointAdapter;

/**
 * The Class NettyWebEndpoint.
 */
public class NettyWebEndpoint extends EndpointAdapter implements Runnable {

	/**
	 * Instantiates a new netty web endpoint.
	 */
	public NettyWebEndpoint() {
		super();
		this.endpointChannelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool());
	}

	/** {@inheritDoc} */
	@Override
	public void create() {
		super.create();
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyWebResponderPipelineFactory());
	}
}
