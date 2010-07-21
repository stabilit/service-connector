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
package com.stabilit.scm.cln;

import com.stabilit.scm.common.scmp.ISCMPCallback;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCMessage;
import com.stabilit.scm.common.service.SynchronousCallback;

/**
 * The Class ServiceCallback. Base class for service callbacks.
 * 
 * @author JTraber
 */
public class ServiceCallback extends SynchronousCallback implements ISCMPCallback {

	/** The message callback. */
	private ISCMessageCallback messageCallback;	

	/**
	 * Instantiates a new ServiceCallback.
	 */
	public ServiceCallback() {
		this(null);
	}

	/**
	 * Instantiates a new ServiceCallback.
	 * 
	 * @param messageCallback
	 *            the message callback
	 */
	public ServiceCallback(ISCMessageCallback messageCallback) {
		this.messageCallback = messageCallback;
		this.synchronous = false;
	}

	@Override
	public void callback(SCMPMessage scmpReply) throws Exception {
		if (this.synchronous) {
			// interested thread waits for message
			super.callback(scmpReply);
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
			// interested thread waits for message
			super.callback(th);
			return;
		}
		this.messageCallback.callback(th);
	}
}
