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
import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.cln.req.RequesterFactory;
import com.stabilit.scm.cln.scmp.SCMPClientSession;

/**
 * @author JTraber
 *
 */
public class SCDataServiceBuilder extends SCServiceBuilder {

	/**
	 * @param host
	 * @param port
	 */
	public SCDataServiceBuilder(String host, int port) {
		super(host,port);
	}

	@Override
	public IService createService(String serviceName) throws Exception {
		IService dataService = new SCDataService();
		RequesterFactory clientFactory = new RequesterFactory();
		IRequester client = clientFactory.newInstance(this.host, this.port,  this.connection, this.numberOfThreads);
		client.connect(); // physical connect
		// attach
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);
		attachCall.setCompression(false);
		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		attachCall.invoke();
		SCMPClientSession scmpSession = new SCMPClientSession(client, serviceName, "");
		scmpSession.createSession();
		dataService.setRequestor(client);
		dataService.setSession(scmpSession);
		return dataService;
	}

}
