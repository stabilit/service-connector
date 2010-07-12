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
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.ISCMessageCallback;
import com.stabilit.scm.cln.service.SCMessage;
import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class ServiceCallback extends SynchronousCallback implements ISCMPCallback {

	private ISCMessageCallback messageCallback;
	private boolean synchronous;

	public ServiceCallback() {
		this(null);
	}

	public ServiceCallback(ISCMessageCallback messageCallback) {
		this.messageCallback = messageCallback;
		this.synchronous = false;
	}

	@Override
	public void callback(SCMPMessage scmpReply) throws Exception {
		if (this.synchronous) {
			super.callback(scmpReply);
			return;
		}
		if (this.messageCallback == null) {
			return;
		}
		SCMessage messageReply = new SCMessage();
		messageReply.setData(scmpReply.getBody());
		messageReply.setCompressed(scmpReply.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION));
		this.messageCallback.callback(messageReply);
	}

	@Override
	public void callback(Throwable th) {
		if (this.synchronous) {
			super.callback(th);
			return;
		}
		if (this.messageCallback == null) {
			return;
		}
		this.messageCallback.callback(th);
	}

	@Override
	public SCMPMessage getReplySynchronous() throws Exception {
		this.synchronous = true;
		return super.getReplySynchronous();
	}
}
