/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.cln.service.SCMessageHandler;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
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

	/** The connection pool. */
	private IConnectionPool connectionPool;
	/** The number of threads to use on client side. */
	private int numberOfThreads;
	/** The connection type, identifies low level component to use for communication (netty, nio). */
	private String conType;
	/** The requester. */
	protected IRequester requester; // becomes a pool later
	/** The attributes. */
	private MapBean<Object> attributes;

	private ServiceConnectorContext context;

	public ServiceConnector(String host, int port) {
		this.host = host;
		this.port = port;
		this.numberOfThreads = 16;
		this.attributes = new MapBean<Object>();
		this.connectionPool = new ConnectionPool(this.host, this.port);
		this.context = new ServiceConnectorContext();
	}

	public ServiceConnector(String host, int port, String conType) {
		this.host = host;
		this.port = port;
		this.conType = conType;
		this.numberOfThreads = 16;
		this.attributes = new MapBean<Object>();
		this.connectionPool = new ConnectionPool(this.host, this.port, this.conType);
		this.context = new ServiceConnectorContext();
	}

	public ServiceConnector(String host, int port, String conType, int keepAliveInterval) {
		this.host = host;
		this.port = port;
		this.conType = conType;
		this.numberOfThreads = 16;
		this.attributes = new MapBean<Object>();
		this.connectionPool = new ConnectionPool(this.host, this.port, this.conType, keepAliveInterval);
		this.context = new ServiceConnectorContext();
	}

	@Override
	public IServiceConnectorContext getContext() {
		return context;
	}

	@Override
	public IFactoryable newInstance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void attach() throws Exception {
		this.requester = new Requester(this.context);
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		attachCall.invoke();
	}

	@Override
	public void detach() throws Exception {
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
		detachCall.invoke();
		// physical disconnect
		this.requester.disconnect();
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
		return conType;
	}

	public void setConnectionType(String conType) {
		this.conType = conType;
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
		return new SessionService(serviceName, this.context);
	}

	@Override
	public void setMaxConnections(int maxConnections) {
		this.connectionPool.setMaxConnections(maxConnections);
	}

	class ServiceConnectorContext implements IServiceConnectorContext {

		@Override
		public IConnectionPool getConnectionPool() {
			return connectionPool;
		}

	}
}
