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
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
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
	protected SCMessageCallback messageCallback;
	/** The service which is using the callback. */
	protected SCService service;

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
			SCMPMessageFault fault = (SCMPMessageFault) scmpReply;
			SCServiceException e = new SCServiceException(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			e.setSCMPError(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			this.messageCallback.receive(e);
			this.service.setRequestComplete();
			return;
		}
		SCMessage messageReply = new SCMessage();
		messageReply.setData(scmpReply.getBody());
		messageReply.setCompressed(scmpReply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		messageReply.setSessionId(scmpReply.getSessionId());
		try {
			messageReply.setMessageInfo(scmpReply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
			if (scmpReply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
				messageReply.setAppErrorCode(scmpReply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
				messageReply.setAppErrorText(scmpReply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			}
		} catch (SCMPValidatorException ex) {
			logger.warn("attributes invalid when setting in scmessage");
		}
		this.messageCallback.receive(messageReply);
		// inform service request is completed
		this.service.setRequestComplete();
	}

	/** {@inheritDoc} */
	@Override
	public void callback(Exception ex) {
		if (this.synchronous) {
			// interested thread waits for message
			super.callback(ex);
			return;
		}
		this.messageCallback.receive(ex);
		// inform service request is completed
		this.service.setRequestComplete();
	}
}