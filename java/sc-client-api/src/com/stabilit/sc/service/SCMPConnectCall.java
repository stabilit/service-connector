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
import com.stabilit.sc.util.DateTimeUtility;

/**
 * @author JTraber
 * 
 */
public class SCMPConnectCall extends SCMPCallAdapter {

	public SCMPConnectCall() {
		this(null);
	}

	public SCMPConnectCall(IClient client) {
		this.client = client;
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(SCMPMsgType.REQ_CONNECT.getRequestName());
		this.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPConnectCall(client);
	}

	public void setVersion(String version) {
		call.setHeader(SCMPHeaderType.SCMP_VERSION.getName(), version);
	}

	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderType.COMPRESSION.getName(), compression);
	}

	private void setLocalDateTime(String localDateTime) {
		call.setHeader(SCMPHeaderType.LOCAL_DATE_TIME.getName(), localDateTime);
	}
	
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		call.setHeader(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName(), keepAliveTimeout);
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		call.setHeader(SCMPHeaderType.KEEP_ALIVE_INTERVAL.getName(), keepAliveInterval);
	}
}
