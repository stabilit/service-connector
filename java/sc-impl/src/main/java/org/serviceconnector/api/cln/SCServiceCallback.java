/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.api.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.SynchronousCallback;

/**
 * The Class SCServiceCallback. Base class for a service callback.
 * 
 * @author JTraber
 */
public class SCServiceCallback extends SynchronousCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCServiceCallback.class);

	/** The message callback. */
	private SCMessageCallback messageCallback;
	/** The service which is using the callback. */
	private SCService service;

	/**
	 * Instantiates a new ServiceCallback.
	 */
	public SCServiceCallback() {
		this(null, null);
	}

	/**
	 * Instantiates a new service callback.
	 * 
	 * @param synchronous
	 *            the synchronous
	 */
	public SCServiceCallback(boolean synchronous) {
		this();
		this.synchronous = synchronous;
	}

	/**
	 * Instantiates a new ServiceCallback.
	 * 
	 * @param messageCallback
	 *            the message callback
	 */
	public SCServiceCallback(SCService service, SCMessageCallback messageCallback) {
		this.service = service;
		this.messageCallback = messageCallback;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(SCMPMessage scmpReply) {
		if (this.synchronous) {
			super.callback(scmpReply);
			return;
		}
		if (scmpReply.isFault()) {
			SCMPFault fault = (SCMPFault) scmpReply;
			String errorCode = fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			if (errorCode != null && errorCode.equals(SCMPError.GATEWAY_TIMEOUT.getErrorCode())) {
				// OTI run out on SC - mark session as dead!
				this.service.inActivateSession();
			}
			Exception ex = fault.getCause();
			if (ex != null && ex instanceof IdleTimeoutException) {
				// OTI run out on client - mark session as dead!
				this.service.inActivateSession();
			}
			SCServiceException e = new SCServiceException(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			this.service.setRequestComplete();
			this.messageCallback.callback(e);
			return;
		}
		SCMessage messageReply = new SCMessage();
		messageReply.setData(scmpReply.getBody());
		messageReply.setCompressed(scmpReply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		messageReply.setSessionId(scmpReply.getSessionId());
		messageReply.setMessageInfo(scmpReply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		// inform service request is completed
		this.service.setRequestComplete();
		this.messageCallback.callback(messageReply);
	}

	/** {@inheritDoc} */
	@Override
	public void callback(Exception ex) {
		if (this.synchronous) {
			// interested thread waits for message
			super.callback(ex);
			return;
		}
		// inform service request is completed
		this.service.setRequestComplete();
		this.messageCallback.callback(ex);
	}
}
