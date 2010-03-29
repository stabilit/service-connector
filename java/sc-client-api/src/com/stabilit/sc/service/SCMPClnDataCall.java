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
package com.stabilit.sc.service;

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPFault;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPClnDataCall extends SCMPCallAdapter {

	public SCMPClnDataCall() {
		this(null);
	}

	public SCMPClnDataCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(SCMPMsgType.REQ_CLN_DATA.getRequestName());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPClnDataCall(client);
	}
	
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
	}
	
	// TODO
	// session id
	
	// bodyLength
	private void setBodyLength(int length) {
		call.setHeader(SCMPHeaderType.BODY_LENGTH.getName(), length);
	}
	// sequencenr
	
	
	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderType.COMPRESSION.getName(), compression);
	}
	
	public void setMessagInfo(String messageInfo) {
		call.setHeader(SCMPHeaderType.MESSAGE_INFO.getName(), messageInfo);
	}
	
	public void setBody(byte[] body) {
		setBodyLength(body.length);
		call.setBody(body);
	}
}
