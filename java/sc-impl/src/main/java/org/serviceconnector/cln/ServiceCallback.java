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
package org.serviceconnector.cln;

import org.apache.log4j.Logger;
import org.serviceconnector.cln.service.Service;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.service.ISCMessageCallback;
import org.serviceconnector.service.SCMessage;
import org.serviceconnector.util.SynchronousCallback;


/**
 * The Class ServiceCallback. Base class for a service callback.
 * 
 * @author JTraber
 */
public class ServiceCallback extends SynchronousCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceCallback.class);

	/** The message callback. */
	private ISCMessageCallback messageCallback;
	/** The service which is using the callback. */
	private Service service;

	/**
	 * Instantiates a new ServiceCallback.
	 */
	public ServiceCallback() {
		this(null, null);
	}

	/**
	 * Instantiates a new service callback.
	 * 
	 * @param synchronous
	 *            the synchronous
	 */
	public ServiceCallback(boolean synchronous) {
		this();
		this.synchronous = synchronous;
	}

	/**
	 * Instantiates a new ServiceCallback.
	 * 
	 * @param messageCallback
	 *            the message callback
	 */
	public ServiceCallback(Service service, ISCMessageCallback messageCallback) {
		this.service = service;
		this.messageCallback = messageCallback;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(SCMPMessage scmpReply) {
		if (this.synchronous) {
			// interested thread waits for message
			super.callback(scmpReply);
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
