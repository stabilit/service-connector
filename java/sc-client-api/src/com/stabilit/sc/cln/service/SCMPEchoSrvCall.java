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

import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;

/**
 * @author JTraber
 * 
 */
public class SCMPEchoSrvCall extends SCMPCallAdapter {

	public SCMPEchoSrvCall() {
		this(null, null);
	}

	public SCMPEchoSrvCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(getMessageType().getRequestName());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPEchoSrvCall(client, scmpSession);
	}

	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
	}

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.ECHO_SRV;
	}
	
	public void setHeader(Map<String, String> header) {
		this.call.setHeader(header);		
	}
	
	public void setMaxNodes(int maxNodes) {
		this.call.setHeader(SCMPHeaderType.MAX_NODES.getName(), String.valueOf(maxNodes));
	}
	
	public void setPartMessage(boolean partMessage) {
		if (partMessage == true) {
			if (this.call.isPart()) {
				return;
			}
			SCMPPart scmpPart = new SCMPPart();
			scmpPart.setHeader(this.call.getHeader());
			scmpPart.setBody(this.call.getBody());
			this.call = scmpPart;
			return;
		}
		if (this.call.isPart() == false) {
			return;			
		}
		SCMP scmp = new SCMP();
		scmp.setHeader(this.call.getHeader());
		scmp.setBody(this.call.getBody());
		this.call = scmp;
		return;
	}
}