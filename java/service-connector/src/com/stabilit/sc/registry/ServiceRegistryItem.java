/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.registry;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.service.SCMPClnEchoCall;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ExceptionListenerSupport;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPSrvCreateSessionCall;
import com.stabilit.sc.service.SCMPSrvDataCall;
import com.stabilit.sc.service.SCMPSrvDeleteSessionCall;
import com.stabilit.sc.service.SCMPSrvEchoCall;
import com.stabilit.sc.service.SCMPSrvSystemCall;
import com.stabilit.sc.srv.client.SCClientFactory;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.srv.ctx.ServerContext;
import com.stabilit.sc.srv.server.IServer;

/**
 * @author JTraber
 * 
 */
public class ServiceRegistryItem extends MapBean<String> implements IFactoryable {

	private IClient client;
	private SCMP registerScmp;
	protected ServiceRegistryItemPool myItemPool;
	private boolean allocated;
	
	public ServiceRegistryItem(SCMP scmp, SocketAddress socketAddress, IServerContext serverContext) {
		this.registerScmp = scmp;
		this.allocated = false;
		this.myItemPool = null;
		this.attrMap = scmp.getHeader();

		SCClientFactory clientFactory = new SCClientFactory();
		int serverPort = Integer.parseInt(registerScmp.getHeader(SCMPHeaderAttributeKey.PORT_NR));
		String serverHost = ((InetSocketAddress) socketAddress).getHostName();
		String serverCon = serverContext.getServer().getServerConfig().getCon();
		client = clientFactory.newInstance(serverHost, serverPort, serverCon);
	}

	public void srvCreateSession(SCMP scmp) throws Exception {
		// TODO client.disconnect and error log
		try {
			client.connect();
		} catch (ConnectionException e) {
			ExceptionListenerSupport.fireException(this, e);
		}
		try {
			SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
					.newInstance(client, scmp);
			createSessionCall.setHeader(scmp.getHeader());
			createSessionCall.invoke();
			this.allocated = true;
		} catch (Exception e) {
			ExceptionListenerSupport.fireException(this, e);
		}
	}

	public void srvDeleteSession(SCMP scmp) throws Exception {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(client, scmp);
		deleteSessionCall.setHeader(scmp.getHeader());
		deleteSessionCall.invoke();
		client.disconnect();
		this.allocated = false;
	}

	public boolean isAllocated() {
		return this.allocated;
	}

	public SCMP clnEcho(SCMP scmp) throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setBody(scmp.getBody());
		return echoCall.invoke();
	}
	
	public SCMP srvEcho(SCMP scmp) throws Exception {
		SCMPSrvEchoCall echoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setHeader(SCMPHeaderAttributeKey.SERVICE_REGISTRY_ID, this.hashCode());
		echoCall.setBody(scmp.getBody());
		return echoCall.invoke();
	}

	public SCMP srvData(SCMP scmp) throws Exception {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(client, scmp);
		srvDataCall.setHeader(scmp.getHeader());
		srvDataCall.setBody(scmp.getBody());
		return srvDataCall.invoke();
	}

	public SCMP srvSystem(SCMP scmp) throws Exception {
		SCMPSrvSystemCall srvSystemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(client,
				scmp);
		srvSystemCall.setHeader(scmp.getHeader());
		srvSystemCall.setBody(scmp.getBody());
		return srvSystemCall.invoke();
	}

	public SCMP clnSystem(SCMP scmp) {
		return null;
	}
	
	@Override
	public IFactoryable newInstance() {
		return this;
	}
}
