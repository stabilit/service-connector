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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SynchronousCallback. Base functionality for getting messages synchronous. Means to wait for a callback.
 * This class is designed to be extended by various callbacks.
 * 
 * @author JTraber
 */
public abstract class SynchronousCallback implements ISCMPSynchronousCallback {

	/** The context. */
	private IContext context;
	/** Queue to store the answer. */
	private final BlockingQueue<SCMPMessage> answer = new LinkedBlockingQueue<SCMPMessage>();

	@Override
	public void callback(SCMPMessage scmpReply) throws Exception {
		this.answer.offer(scmpReply);
	}

	@Override
	public void callback(Throwable th) {
		SCMPMessage fault = new SCMPFault(th);
		this.answer.offer(fault);
	}

	@Override
	public IContext getContext() {
		return this.context;
	}

	@Override
	public void setContext(IContext context) {
		this.context = context;
	}

	@Override
	public SCMPMessage getMessageSync() throws Exception {
		// the method take() from BlockingQueue waits inside
		SCMPMessage reply = this.answer.take();
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException(fault.getCause());
		}
		return reply;
	}
}
