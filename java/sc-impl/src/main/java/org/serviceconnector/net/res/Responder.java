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

import org.apache.log4j.Logger;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.ctx.AppContext;

/**
 * The Class Responder. Abstracts responder functionality from a application view. It is not the technical
 * representation of a responder connection.
 * 
 * @author JTraber
 */
public class Responder implements IResponder {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(Responder.class);

	/** The responder configuration. */
	private CommunicatorConfig respConfig;
	/** The endpoint connection. */
	private IEndpoint endpoint;

	public Responder() {
	}

	public Responder(CommunicatorConfig respConfig) {
		this.respConfig = respConfig;
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws Exception {
		EndpointFactory endpointFactory = AppContext.getCurrentContext().getEndpointFactory();
		this.endpoint = endpointFactory.createEndpoint(this.respConfig.getConnectionType());
		this.endpoint.setResponder(this);
		this.endpoint.setHost(this.respConfig.getHost());
		this.endpoint.setPort(this.respConfig.getPort());
		this.endpoint.create();
	}

	/** {@inheritDoc} */
	@Override
	public void startListenAsync() throws Exception {
		this.endpoint.startsListenAsync();
	}

	/** {@inheritDoc} */
	@Override
	public void startListenSync() throws Exception {
		this.endpoint.startListenSync();
	}

	/** {@inheritDoc} */
	@Override
	public void stopListening() {
		this.endpoint.stopListening();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		this.endpoint.destroy();
	}

	@Override
	public CommunicatorConfig getResponderConfig() {
		return this.respConfig;
	}

	@Override
	public void setResponderConfig(CommunicatorConfig respConfig) {
		this.respConfig = respConfig;
	}
}
