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

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;

/**
 * @author JTraber
 * 
 */
public class SCMPClnDataCall extends SCMPCallAdapter {

	public SCMPClnDataCall() {
		this(null,null);
	}

	public SCMPClnDataCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}
	
	@Override
	public SCMP invoke() throws Exception {
		super.invoke();
		
		while(this.result.isPart()) {
			String messageInfo = this.call.getHeader(SCMPHeaderType.MESSAGE_INFO.getName());
			this.call = new SCMPPart();
			this.call.setHeader(this.result.getHeader());
			this.call.setHeader(SCMPHeaderType.MESSAGE_INFO.getName(), messageInfo);
			super.invoke();
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPClnDataCall(client, scmpSession);
	}

	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
	}

	public void setMessagInfo(String messageInfo) {
		call.setHeader(SCMPHeaderType.MESSAGE_INFO.getName(), messageInfo);
	}
	
	//TODO sequenceNr

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.REQ_CLN_DATA;
	}
}
