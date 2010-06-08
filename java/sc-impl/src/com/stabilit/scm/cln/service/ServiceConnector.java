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
package com.stabilit.scm.cln.service;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.scmp.SCMPServiceSession;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterFactory;

/**
 * @author JTraber
 */
public class ServiceConnector implements IServiceConnector {

	private String connection;
	private int numberOfThreads;
	private String host;
	private int port;
	private ServiceConnectorContext serviceConnectorCtx;
	private IRequester requester; // becomes a pool later
	private RequesterFactory clientFactory;

	public ServiceConnector(String host, int port) {
		this.host = host;
		this.port = port;
		this.serviceConnectorCtx = new ServiceConnectorContext();
		this.serviceConnectorCtx.setAttribute("port", this.port);
		this.serviceConnectorCtx.setAttribute("host", this.host);
		this.clientFactory = new RequesterFactory();		
	}

	@Override
	public void connect() throws Exception {
		requester = clientFactory.newInstance(this.host, this.port, this.connection, this.numberOfThreads);
		requester.connect();
		// attach
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(requester);
		attachCall.setCompression(false);
		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		attachCall.invoke();	
	}

	@Override
	public ISession createDataSession(String serviceName) throws Exception {		
		SCMPServiceSession scmpSession = new SCMPServiceSession(requester, serviceName, "");
		scmpSession.createSession();
		ISession scDataSession = new SCDataSession(requester, scmpSession);
		return scDataSession;
	}

	@Override
	public void disconnect() throws Exception {
		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(requester);
		detachCall.invoke();

		this.requester.disconnect(); // physical disconnect
		this.requester.destroy();
	}

	@Override
	public IServiceConnectorContext getSCContext() {
		return serviceConnectorCtx;
	}

	@Override
	public void setAttribute(String string, int i) {
	}
}
