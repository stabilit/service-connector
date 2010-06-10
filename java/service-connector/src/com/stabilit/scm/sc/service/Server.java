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
package com.stabilit.scm.sc.service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.ctx.IResponderContext;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterFactory;
import com.stabilit.scm.common.registry.ResponderRegistry;
import com.stabilit.scm.common.util.MapBean;

/**
 * @author JTraber
 */
public class Server extends MapBean<String> {

	private String host;
	private int portNr;
	private int maxSessions;
	private List<IRequester> listOfRequesters;
	private SocketAddress socketAddress;

	public Server(InetSocketAddress socketAdress, int portNr, int maxSessions) {
		this.socketAddress = socketAdress;
		this.portNr = portNr;
		this.maxSessions = maxSessions;
		ResponderRegistry responderRegistry = ResponderRegistry.getCurrentInstance();
		IResponderContext respContext = responderRegistry.getCurrentContext();
		IResponderConfigItem serverConfig = respContext.getResponder().getResponderConfig();
		// The connection key, identifies low level component to use (netty, nio)
		String connectionKey = serverConfig.getConnection();
		int numberOfThreads = serverConfig.getNumberOfThreads();
		this.host = socketAdress.getHostName();
		this.listOfRequesters = new ArrayList<IRequester>();

		//init list of requesters
		RequesterFactory reqFactory = new RequesterFactory();
		IRequester req = null;		
		for (int i = 0; i < maxSessions; i++) {
			 req = reqFactory.newInstance(this.host, this.portNr, connectionKey, numberOfThreads);
			 listOfRequesters.add(req);
		}
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void immediateConnect() throws Exception {		
		for (IRequester req : listOfRequesters) {
			req.connect();
		}
	}

	public String getHost() {
		return host;
	}

	public int getPortNr() {
		return portNr;
	}

	public int getMaxSessions() {
		return maxSessions;
	}
	
	public void destroy() {
		for (IRequester req : listOfRequesters) {
			try {
				req.disconnect();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
				continue;
			}
		}
	}
}
