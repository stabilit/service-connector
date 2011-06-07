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
package org.serviceconnector.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.ISCMPSynchronousCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SynchronousCallback. Base functionality for getting messages synchronous. Means to wait for a call for the callback.
 * This class is designed to be extended by various callback's. It provides synchronous flag to to save state if somebody is waiting
 * for a message. Synchronous flag might be useful in subclasses. It only gets access to the newest message - it only queues one
 * item. Queuing an arrived message happens only if someone is expecting (synchronous = true) a reply. This restriction prevents
 * race
 * conditions - late messages are ignored.
 * 
 * @author JTraber
 */
public abstract class SynchronousCallback implements ISCMPSynchronousCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SynchronousCallback.class);

	/** Queue to store the answer. */
	protected final BlockingQueue<SCMPMessage> answer;
	/** The synchronous, marks if somebody waits for the message. */
	protected volatile boolean synchronous;

	/**
	 * Instantiates a new synchronous callback.
	 */
	public SynchronousCallback() {
		this.synchronous = false;
		this.answer = new ArrayBlockingQueue<SCMPMessage>(1);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage scmpReply) {
		if (this.synchronous == false) {
			// offering is only allowed if someone is expecting a message - prevents race conditions, an answer might
			// arrive late after operation timeout already run out, can be ignored
			return;
		}
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
	public void receive(Exception ex) {
		if (this.synchronous == false) {
			// offering is only allowed if someone is expecting a message - prevents race conditions, an answer might
			// arrive late after operation timeout already run out, can be ignored
			return;
		}
		SCMPMessage fault = new SCMPMessageFault(ex);
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
	public SCMPMessage getMessageSync(int timeoutMillis) {
		if (timeoutMillis <= 0) {
			// timeoutMillis must be greater than 0
			LOGGER.error("timeoutMillis <= 0");
			return null;
		}
		// set synchronous mode
		this.synchronous = true;
		SCMPMessage reply = null;
		try {
			// the method poll() from BlockingQueue waits inside
			reply = this.answer.poll(timeoutMillis, TimeUnit.MILLISECONDS);
			// reset synchronous mode
			this.synchronous = false;
		} catch (InterruptedException e) {
			// finally clause handles exception too
			LOGGER.warn("InterruptedException when waiting for message " + e);
		} finally {
			if (reply == null) {
				// time runs out before message got received
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.REQUEST_WAIT_ABORT, "");
				fault.setMessageType(SCMPMsgType.UNDEFINED);
				LOGGER.error("Operation did not complete in time, aborting callback=" + this.getClass().getName());
				return fault;
			}
		}
		return reply;
	}
}
