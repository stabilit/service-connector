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

import com.stabilit.scm.common.ctx.ResponderContext;
import com.stabilit.scm.srv.config.IResponderConfigItem;
import com.stabilit.scm.srv.ctx.IResponderContext;
import com.stabilit.scm.srv.res.IEndpoint;
import com.stabilit.scm.srv.res.IResponder;

/**
 * The Class Responder. Abstracts responder functionality from a application view. It is not the technical representation
 * of a responder connection.
 * 
 * @author JTraber
 */
public abstract class Responder implements IResponder {

	/** The responder configuration. */
	private IResponderConfigItem respConfig;
	/** The endpoint connection. */
	private IEndpoint endpoint;
	/** The responder context. */
	protected IResponderContext respContext;

	/** {@inheritDoc} */
	@Override
	public void setResponderConfig(IResponderConfigItem respConfig) {
		this.respConfig = respConfig;
		this.respContext = new ResponderContext(this);
		EndpointFactory endpointFactory = new EndpointFactory();
		this.endpoint = endpointFactory.newInstance(this.respConfig.getConnection());
		this.endpoint.setResponder(this);
		this.endpoint.setHost(this.respConfig.getHost());
		this.endpoint.setPort(this.respConfig.getPort());
		this.endpoint.setNumberOfThreads(this.respConfig.getNumberOfThreads());
	}

	/** {@inheritDoc} */
	@Override
	public void create() throws Exception {
		endpoint.create();
	}

	/** {@inheritDoc} */
	@Override
	public void runAsync() throws Exception {
		endpoint.runAsync();
	}

	/** {@inheritDoc} */
	@Override
	public void runSync() throws Exception {
		endpoint.runSync();
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		endpoint.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public IResponderContext getResponderContext() {
		return respContext;
	}

	/** {@inheritDoc} */
	@Override
	public IResponderConfigItem getResponderConfig() {
		return respConfig;
	}
}
