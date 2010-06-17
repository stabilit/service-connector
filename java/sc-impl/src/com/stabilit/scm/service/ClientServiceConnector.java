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
package com.stabilit.scm.service;

import java.io.InputStream;
import java.io.OutputStream;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.cln.service.ISCSession;
import com.stabilit.scm.cln.service.ISCSubscription;
import com.stabilit.scm.cln.service.SCMessageHandler;
import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class ServiceConnector. Represents the service connector on client side. Provides functions to create sessions to
 * a server and to connect/disconnect to an SC. This component is responsible to observe the availability of the SC. If
 * the SC gets unreachable every open session has to be deleted.
 * 
 * @author JTraber
 */
class ClientServiceConnector implements IClientServiceConnector {

	/** The host of the SC. */
	public String host;
	/** The port of the SC. */
	public int port;
	/** The number of threads to use on client side. */
	public int numberOfThreads;
	/** The connection key, identifies low level component to use for communication (netty, nio). */
	public String connectionKey;
	/** The requester. */
	private IRequester requester; // becomes a pool later
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
	public ClientServiceConnector(String host, int port) {
		this.host = host;
		this.port = port;
		this.connectionKey = "netty.http"; // default is netty http
		this.numberOfThreads = 16; // default is 16 threads
		attributes = new MapBean<Object>();
	}

	/**
	 * Connect to SC. With this connect observing the SC starts.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void connect() throws Exception {
		requester = new Requester();
		IRequesterConfigItem config = new RequesterConfig().new RequesterConfigItem(this.host, this.port,
				this.connectionKey, this.numberOfThreads);
		requester.setRequesterConfig(config);
		requester.connect();
		// sets up the attach call
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(requester);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		// attaches client
		attachCall.invoke();
	}

	/**
	 * New data session. Data session allows using a service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the iSC session
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public ISCSession newDataSession(String serviceName) throws Exception {
		SCDataSession scDataSession = new SCDataSession(serviceName, requester);
		return scDataSession;
	}

	/**
	 * Disconnect from SC. Every open session needs to be closed.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void disconnect() throws Exception {
		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(requester);
		detachCall.invoke();

		this.requester.disconnect(); // physical disconnect
		this.requester.destroy();
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
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
	public void downloadFile(String string, String sourceFileName, OutputStream outStream) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void uploadFile(String string, String targetFileName, InputStream inStream) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISCSubscription newSubscription(String string, SCMessageHandler messageHandler, String mask) {
		throw new UnsupportedOperationException();
	}
}
