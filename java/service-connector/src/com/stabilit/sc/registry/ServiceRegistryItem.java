/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.registry;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

import com.stabilit.sc.cln.client.ConnectionException;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.service.SCMPEchoCall;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderType;
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
	private static Logger log = Logger.getLogger(ServiceRegistryItem.class);
	
	public ServiceRegistryItem(SCMP scmp, SocketAddress socketAddress) {
		this.registerScmp = scmp;
		this.attrMap = scmp.getHeader();

		IServerContext currentServerContext = ServerContext.getCurrentInstance();
		IServer server = currentServerContext.getServer();

		SCClientFactory clientFactory = new SCClientFactory();
		int serverPort = Integer.parseInt(registerScmp.getHeader(SCMPHeaderType.PORT_NR.getName()));
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

	public SCMP echo(SCMP scmp) throws Exception {
		SCMPEchoCall echoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(client, scmp);
		echoCall.setHeader(scmp.getHeader());
		echoCall.setBody(scmp.getBody());
		echoCall.setTransitive(false);
		return echoCall.invoke();
	}

	public SCMP srvData(SCMP scmp) throws Exception {
		SCMPSrvDataCall srvDataCall = (SCMPSrvDataCall) SCMPCallFactory.SRV_DATA_CALL.newInstance(client, scmp);
		srvDataCall.setHeader(scmp.getHeader());
		srvDataCall.setBody(scmp.getBody());
		return srvDataCall.invoke();
	}
}
