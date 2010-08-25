/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package com.stabilit.scm.cln.service;

import org.apache.log4j.Logger;

import com.stabilit.scm.cln.call.SCMPCallAdapter;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.service.ISCContext;

/**
 * The Class Service. Provides basic stuff for every kind of remote service interfaces.
 */
public abstract class Service {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(Service.class);
	
	/** The service name. */
	protected String serviceName;
	/** The session id, identifies current session context. */
	protected String sessionId;
	/** The service context. */
	protected IServiceContext serviceContext;
	/** The requester to communicate. */
	protected IRequester requester;
	/** The callback to use by service. */
	protected ISCMPSynchronousCallback callback;
	/** The pending request, marks if a reply is outstanding or if service is ready for next. */
	protected boolean pendingRequest;
	/** The message id. */
	protected SCMPMessageId msgId;

	/**
	 * Instantiates a new service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param context
	 *            the context
	 */
	public Service(String serviceName, ISCContext context) {
		this.serviceName = serviceName;
		this.sessionId = null;
		this.callback = null;
		this.pendingRequest = false;
		this.msgId = new SCMPMessageId();
	}

	/**
	 * Sets the request complete.
	 */
	public void setRequestComplete() {
		this.pendingRequest = false;
	}

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public IServiceContext getContext() {
		return this.serviceContext;
	}
}
