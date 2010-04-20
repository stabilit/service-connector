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
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPPart;

/**
 * @author JTraber
 * 
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	protected IClient client;
	protected SCMP scmpSession;
	protected SCMP call;
	protected SCMP result;

	public SCMPCallAdapter() {
		this(null, null);
	}

	public SCMPCallAdapter(IClient client, SCMP scmpSession) {
		this.client = client;

		this.scmpSession = scmpSession;

		if (this.scmpSession != null) {
			if (this.scmpSession.isPart()) {
				this.call = new SCMPPart();
				this.call.setHeader(this.scmpSession.getHeader());
			} else {
				this.call = new SCMP();
			}
			this.call.setSessionId(scmpSession.getSessionId());
			this.call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmpSession
					.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		}

		if (this.call == null) {
			this.call = new SCMP();
		}
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		throw new UnsupportedOperationException();
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
	public SCMP getCall() {
		return call;
	}

	@Override
	public SCMP getResult() {
		return result;
	}

	public void setBody(Object obj) {
		call.setBody(obj);
	}

	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}
}