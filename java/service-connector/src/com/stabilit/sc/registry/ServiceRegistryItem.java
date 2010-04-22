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
package com.stabilit.sc.registry;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.service.SCMPEchoSrvCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.service.SCMPAllocateSessionCall;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPDeAllocateSessionCall;
import com.stabilit.sc.service.SCMPSrvDataCall;
import com.stabilit.sc.srv.client.SCClientFactory;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.ctx.ServerContext;
import com.stabilit.sc.srv.server.IServer;

/**
 * @author JTraber
 * 
 */
public class ServiceRegistryItem extends MapBean<String> {

	private IClient client;
	private SCMP registerScmp;
	
	public ServiceRegistryItem(SCMP scmp, SocketAddress socketAddress) {
		this.registerScmp = scmp;
		this.attrMap = scmp.getHeader();

		IServerContext currentServerContext = ServerContext.getCurrentInstance();
		IServer server = currentServerContext.getServer();

		SCClientFactory clientFactory = new SCClientFactory();
		int serverPort = Integer.parseInt(registerScmp.getHeader(SCMPHeaderAttributeKey.PORT_NR));
		String serverHost = ((InetSocketAddress) socketAddress).getHostName();
		String serverCon = server.getServerConfig().getCon();
		client = clientFactory.newInstance(serverHost, serverPort, serverCon);
	}

	public void allocate(SCMP scmp) throws Exception {
		// TODO client.disconnect and error log
		try {
			client.connect();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		try {
			SCMPAllocateSessionCall allocateSessionCall = (SCMPAllocateSessionCall) SCMPCallFactory.ALLOCATE_SESSION_CALL
					.newInstance(client, scmp);
			allocateSessionCall.setHeader(scmp.getHeader());
			allocateSessionCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deallocate(SCMP scmp) throws Exception {
		SCMPDeAllocateSessionCall deAllocateSessionCall = (SCMPDeAllocateSessionCall) SCMPCallFactory.DEALLOCATE_SESSION_CALL
				.newInstance(client, scmp);
		deAllocateSessionCall.setHeader(scmp.getHeader());
		deAllocateSessionCall.invoke();
		client.disconnect();
	}

	public boolean isAllocated() {
		return false;
	}

	public SCMP echoSrv(SCMP scmp) throws Exception {
		SCMPEchoSrvCall echoCall = (SCMPEchoSrvCall) SCMPCallFactory.ECHO_SRV_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setBody(scmp.getBody());
		return echoCall.invoke();
	}

	public SCMP srvData(SCMP scmp) throws Exception {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(client, scmp);
		srvDataCall.setHeader(scmp.getHeader());
		srvDataCall.setBody(scmp.getBody());
		return srvDataCall.invoke();
	}
}
