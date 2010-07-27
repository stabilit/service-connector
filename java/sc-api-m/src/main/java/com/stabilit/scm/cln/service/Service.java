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
package com.stabilit.scm.cln.service;

import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.service.ISCContext;

public abstract class Service {

	protected String serviceName;
	protected String sessionId;
	protected IServiceContext serviceContext;
	protected IRequester requester;
	protected ISCMPSynchronousCallback callback;
	protected boolean pendingRequest;
	protected SCMPMessageId msgId;
	
	public Service(String serviceName, ISCContext context) {
		this.serviceName = serviceName;
		this.sessionId = null;
		this.callback = null;
		this.pendingRequest = false;
		this.msgId = null;
	}

	public void setRequestComplete() {
		this.pendingRequest = false;
	}
	
	public IServiceContext getContext() {
		return this.serviceContext;
	}
}
