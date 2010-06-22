/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessageHandler;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.util.MapBean;

/**
 * @author JTraber
 */
public class ServiceConnector implements IServiceConnector {

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The number of threads to use on client side. */
	private int numberOfThreads;
	/** The connection key, identifies low level component to use for communication (netty, nio). */
	private String connectionKey;
	/** The requester. */
	protected IRequester requester; // becomes a pool later
	/** The attributes. */
	private MapBean<Object> attributes;

	/**
	 * Instantiates a new service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public ServiceConnector(String host, int port, String connectionKey, int numberOfThreads) {
		this.host = host;
		this.port = port;
		this.connectionKey = connectionKey;
		this.numberOfThreads = numberOfThreads;
		this.attributes = new MapBean<Object>();
	}

	@Override
	public void attach() throws Exception {
		this.requester = new Requester();
		ICommunicatorConfig config = new CommunicatorConfig("server-requester", this.host, this.port,
				this.connectionKey, this.numberOfThreads, 1000);
		this.requester.setRequesterConfig(config);
		// TODO attach call
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		attachCall.invoke();
	}

	@Override
	public void detach() throws Exception {

		// TODO detach
		try {
			// physical disconnect
			this.requester.disconnect();
		} finally {
			// clean up in any case
			this.requester.destroy();
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attributes.setAttribute(name, value);
	}

	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public String getConnectionKey() {
		return connectionKey;
	}

	public void setConnectionKey(String connectionKey) {
		this.connectionKey = connectionKey;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public IFileService newFileService(String serviceName) {

		return null;
	}

	@Override
	public IPublishService newPublishingService(SCMessageHandler messageHandler, String serviceName) {

		return null;
	}

	@Override
	public ISessionService newSessionService(String serviceName) {

		return null;
	}
}
