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
import com.stabilit.sc.io.IpAddressList;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPFault;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.service.ISCMPCall;
import com.stabilit.sc.service.SCMPCallAdapter;
import com.stabilit.sc.service.SCMPServiceException;

/**
 * @author JTraber
 * 
 */
public class SCMPDeAllocateSessionCall extends SCMPCallAdapter {

	public SCMPDeAllocateSessionCall() {
		this(null);
	}

	public SCMPDeAllocateSessionCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(SCMPMsgType.REQ_DEALLOCATE_SESSION.getRequestName());

		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPDeAllocateSessionCall(client);
	}
	
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
	}
	
	public void setSessionId(String sessionId) {
		call.setHeader(SCMPHeaderType.SESSION_ID.getName(), sessionId);
	}

	public void setIpAddressList(IpAddressList ipAddressList) {
		call.setHeader(SCMPHeaderType.IP_ADDRESS_LIST.getName(), ipAddressList.toString());
	}
	
	public void setSessionInfo(String sessionInfo) {
		call.setHeader(SCMPHeaderType.SESSION_INFO.getName(), sessionInfo);
	}
}
