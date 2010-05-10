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

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPClnEchoCall;
import com.stabilit.sc.cln.call.SCMPSrvCreateSessionCall;
import com.stabilit.sc.cln.call.SCMPSrvDataCall;
import com.stabilit.sc.cln.call.SCMPSrvDeleteSessionCall;
import com.stabilit.sc.cln.call.SCMPSrvEchoCall;
import com.stabilit.sc.cln.call.SCMPSrvSystemCall;
import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.srv.client.SCClientFactory;
import com.stabilit.sc.srv.ctx.IServerContext;
import com.stabilit.sc.util.MapBean;

/**
 * The Class ServiceRegistryItem. Provides access to a service. Gets initialized when service registers and saves
 * service information. Holds a <code>ServiceRegistryItemPool</code> to manage incoming requests.
 * 
 * @author JTraber
 */
public class ServiceRegistryItem extends MapBean<String> implements IFactoryable {

	/** The client. */
	private IClient client;
	/** The register scmp. */
	private SCMP registerScmp;
	/** The my item pool. */
	protected ServiceRegistryItemPool myItemPool;
	/** The allocated. */
	private boolean allocated;

	/**
	 * Instantiates a new service registry item.
	 * 
	 * @param scmp
	 *            the scmp
	 * @param socketAddress
	 *            the socket address
	 * @param serverContext
	 *            the server context
	 */
	public ServiceRegistryItem(SCMP scmp, SocketAddress socketAddress, IServerContext serverContext) {
		this.registerScmp = scmp;
		this.allocated = false;
		this.myItemPool = null;
		this.attrMap = scmp.getHeader();

		// setting up client to connect backend server
		SCClientFactory clientFactory = new SCClientFactory();
		int serverPort = Integer.parseInt(registerScmp.getHeader(SCMPHeaderAttributeKey.PORT_NR));
		String serverHost = ((InetSocketAddress) socketAddress).getHostName();
		String serverCon = serverContext.getServer().getServerConfig().getCon();
		client = clientFactory.newInstance(serverHost, serverPort, serverCon);
	}

	/**
	 * Srv create session. Creates a session on a backend server.
	 * 
	 * @param scmp
	 *            the scmp
	 * @throws Exception
	 *             the exception
	 */
	public void srvCreateSession(SCMP scmp) throws Exception {
		// TODO client.disconnect and error log
		try {
			client.connect();
		} catch (ConnectionException e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
		}
		try {
			SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
					.newInstance(client, scmp);
			createSessionCall.setHeader(scmp.getHeader());
			createSessionCall.invoke();
			this.allocated = true;
		} catch (Exception e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
		}
	}

	/**
	 * Srv delete session. Deletes a session on a server.
	 * 
	 * @param scmp
	 *            the scmp
	 * @throws Exception
	 *             the exception
	 */
	public void srvDeleteSession(SCMP scmp) throws Exception {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(client, scmp);
		deleteSessionCall.setHeader(scmp.getHeader());
		deleteSessionCall.invoke();
		client.disconnect();
		this.allocated = false;
	}

	/**
	 * Checks if is allocated.
	 * 
	 * @return true, if is allocated
	 */
	public boolean isAllocated() {
		return this.allocated;
	}

	/**
	 * Cln echo.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	public SCMP clnEcho(SCMP scmp) throws Exception {
		SCMPClnEchoCall echoCall = (SCMPClnEchoCall) SCMPCallFactory.CLN_ECHO_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setBody(scmp.getBody());
		return echoCall.invoke();
	}

	/**
	 * Srv echo.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	public SCMP srvEcho(SCMP scmp) throws Exception {
		SCMPSrvEchoCall echoCall = (SCMPSrvEchoCall) SCMPCallFactory.SRV_ECHO_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setHeader(SCMPHeaderAttributeKey.SERVICE_REGISTRY_ID, this.hashCode());
		echoCall.setBody(scmp.getBody());
		return echoCall.invoke();
	}

	/**
	 * Srv data.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	public SCMP srvData(SCMP scmp) throws Exception {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(client, scmp);
		srvDataCall.setHeader(scmp.getHeader());
		srvDataCall.setBody(scmp.getBody());
		return srvDataCall.invoke();
	}

	/**
	 * Srv system.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 * @throws Exception
	 *             the exception
	 */
	public SCMP srvSystem(SCMP scmp) throws Exception {
		SCMPSrvSystemCall srvSystemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(client,
				scmp);
		srvSystemCall.setHeader(scmp.getHeader());
		srvSystemCall.setBody(scmp.getBody());
		return srvSystemCall.invoke();
	}

	/**
	 * Cln system.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the sCMP
	 */
	public SCMP clnSystem(SCMP scmp) {
		return null;
	}

	/**
	 * New instance.
	 * 
	 * @return the i factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}
}
