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
package com.stabilit.sc.cln.service;

import java.net.InetAddress;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPCreateSessionCall extends SCMPCallAdapter {
	
	public SCMPCreateSessionCall() {
		this(null);
	}
	
	public SCMPCreateSessionCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMPSession invoke() throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		this.call.setHeader(SCMPHeaderAttributeType.IP_ADDRESS_LIST.getName(),localHost.getHostAddress());
		super.invoke();
		SCMPSession scmpSession = new SCMPSession(this.result); // register session in internal registry
		scmpSession.addSessionRegistry();
		return scmpSession;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPCreateSessionCall(client);
	}
	
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeType.SERVICE_NAME.getName(), serviceName);
	}
	
	public void setSessionInfo(String sessionInfo) {
		call.setHeader(SCMPHeaderAttributeType.SESSION_INFO.getName(), sessionInfo);
	}
	
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CREATE_SESSION;
	}
}
