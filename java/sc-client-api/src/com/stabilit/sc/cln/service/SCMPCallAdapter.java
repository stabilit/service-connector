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
import com.stabilit.sc.cln.io.SCMPSession;
import com.stabilit.sc.common.io.IMessage;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	protected IClient client;
	protected SCMPSession scmpSession;
	protected SCMP call;
	protected SCMP result;

	/**
	 * 
	 */
	public SCMPCallAdapter() {
		super();
		this.call = new SCMP();
	}

	public SCMPCallAdapter(IClient client, SCMPSession scmpSession) {
		this.client = client;
		this.scmpSession = scmpSession;
		this.call.setMessageType(SCMPMsgType.REQ_DELETE_SESSION.getRequestName());
		this.call.setSessionId(scmpSession.getSessionId());
		this.call.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), scmpSession
				.getHeader(SCMPHeaderType.SERVICE_NAME.getName()));
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMPSession scmpSession) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SCMP invoke() throws Exception {
		this.result = client.sendAndReceive(this.call);

		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public SCMP getCall() {
		return call;
	}

	@Override
	public SCMP getResult() {
		return result;
	}

	public void setBody(byte[] buffer) {
		if (buffer == null) {
			throw new IllegalArgumentException();
		}
		call.setBody(buffer);
	}

	public void setBody(String s) {
		if (s == null) {
			throw new IllegalArgumentException();
		}
		call.setBody(s);
	}

	public void setBody(IMessage message) {
		if (message == null) {
			throw new IllegalArgumentException();
		}
		call.setBody(message);
	}

	// sequencenr

	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderType.COMPRESSION.getName(), compression);
	}

}