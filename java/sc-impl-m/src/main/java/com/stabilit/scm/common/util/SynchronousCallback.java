/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.naming.CommunicationException;

import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SynchronousCallback. Base functionality for getting messages synchronous. Means to wait for a call for the
 * callback. This class is designed to be extended by various callback's. It provides synchronous flag to to save state
 * if somebody is waiting for a message. Synchronous flag might be useful in subclasses. It only gets access to the
 * newest message - its not a queuing mechanism.
 * 
 * @author JTraber
 */
public abstract class SynchronousCallback implements ISCMPSynchronousCallback {

	/** Queue to store the answer. */
	private final BlockingQueue<SCMPMessage> answer; 
	/** The synchronous, marks if somebody waits for the message. */
	protected boolean synchronous;

	public SynchronousCallback() {
		this.synchronous = false;
		this.answer = new ArrayBlockingQueue<SCMPMessage>(1);
	}
	
	/** {@inheritDoc} */
	@Override
	public void callback(SCMPMessage scmpReply) throws Exception {
		if (this.answer.offer(scmpReply)) {
			// queue empty object can be added
			return;
		}
		// object could not be added - clear queue and offer again
		this.answer.clear();
		this.answer.offer(scmpReply);
	}

	/** {@inheritDoc} */
	@Override
	public void callback(Throwable th) {
		SCMPMessage fault = new SCMPFault(th);
		if (this.answer.offer(fault)) {
			// queue empty object can be added
			return;
		}
		// object could not be added - clear queue and offer again
		this.answer.clear();
		this.answer.offer(fault);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getMessageSync() throws Exception {
		// set synchronous mode
		this.synchronous = true;
		// the method take() from BlockingQueue waits inside
		SCMPMessage reply = this.answer.take();
		// reset synchronous mode
		this.synchronous = false;
		if (reply == null) {
			// time runs out before message got received
			throw new CommunicationException("receiving message failed. Getting message synchronous failed.");
		}
		return reply;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getMessageSync(int timeoutInMillis) throws Exception {
		// set synchronous mode
		this.synchronous = true;
		// the method poll() from BlockingQueue waits inside
		SCMPMessage reply = this.answer.poll(timeoutInMillis, TimeUnit.MILLISECONDS);
		// reset synchronous mode
		this.synchronous = false;
		if (reply == null) {
			// time runs out before message got received
			throw new CommunicationException("time for receiving message run out. Getting message synchronous failed.");
		}
		return reply;
	}
}
