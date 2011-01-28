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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.ctx.AppContext;

/**
 * The Class Responder. Abstracts responder functionality from a application view. It is not the technical representation of a
 * responder connection.
 * 
 * @author JTraber
 */
public class Responder implements IResponder {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(Responder.class);

	/** The responder configuration. */
	private CommunicatorConfig respConfig;
	/** The endpoint connection. */
	private List<IEndpoint> endpoints;

	public Responder() {
		this(null);
	}

	public Responder(CommunicatorConfig respConfig) {
		this.respConfig = respConfig;
		this.endpoints = new ArrayList<IEndpoint>();
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws Exception {
		for (String host : this.respConfig.getInterfaces()) {
			EndpointFactory endpointFactory = AppContext.getEndpointFactory();
			IEndpoint endpoint = endpointFactory.createEndpoint(this.respConfig.getConnectionType());
			endpoint.setResponder(this);
			endpoint.setHost(host);
			endpoint.setPort(this.respConfig.getPort());
			endpoint.create();
			endpoints.add(endpoint);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListenAsync() throws Exception {
		for (IEndpoint endpoint : this.endpoints) {
			endpoint.startsListenAsync();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void startListenSync() throws Exception {
		for (IEndpoint endpoint : this.endpoints) {
			endpoint.startListenSync();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
		for (IEndpoint endpoint : this.endpoints) {
			endpoint.stopListening();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		for (IEndpoint endpoint : this.endpoints) {
			endpoint.destroy();
		}
	}

	/** {@inheritDoc} */
	@Override
	public CommunicatorConfig getResponderConfig() {
		return this.respConfig;
	}

	/** {@inheritDoc} */
	@Override
	public void setResponderConfig(CommunicatorConfig respConfig) {
		this.respConfig = respConfig;
	}

	/**
	 * Gets the endpoint.
	 * 
	 * @return the endpoint
	 */
	public List<IEndpoint> getEndpoints() {
		return Collections.unmodifiableList(endpoints);
	}
}
