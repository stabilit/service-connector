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
package com.stabilit.sc.common.net.res;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.scmp.SCMPMessageId;
import com.stabilit.sc.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.sc.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class SCMPSessionCompositeItem. Item represents a value in SCMPSessionCompositeRegistry. Gives access to
 * composite receiver/sender and in the context used SCMPMessageId.
 * 
 * @author JTraber
 */
public class SCMPSessionCompositeItem {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPSessionCompositeItem.class);	
	
	/** The receiver. */
	private SCMPCompositeReceiver receiver;
	/** The sender. */
	private SCMPCompositeSender sender;
	/** The message id. */
	private SCMPMessageId messageId;

	/**
	 * Instantiates a new sCMP session composite item.
	 */
	public SCMPSessionCompositeItem() {
		this(null, null);
	}

	/**
	 * Instantiates a new sCMP session composite item.
	 * 
	 * @param receiver
	 *            the receiver
	 * @param sender
	 *            the sender
	 */
	public SCMPSessionCompositeItem(SCMPCompositeReceiver receiver, SCMPCompositeSender sender) {
		super();
		this.receiver = receiver;
		this.sender = sender;
		this.messageId = new SCMPMessageId();
	}

	/**
	 * Gets the composite receiver.
	 * 
	 * @return the composite receiver
	 */
	public SCMPCompositeReceiver getCompositeReceiver() {
		return receiver;
	}

	/**
	 * Gets the composite sender.
	 * 
	 * @return the composite sender
	 */
	public SCMPCompositeSender getCompositeSender() {
		return sender;
	}

	/**
	 * Gets the message id.
	 * 
	 * @return the message id
	 */
	public SCMPMessageId getMessageId() {
		return messageId;
	}

	/**
	 * Sets the composite receiver.
	 * 
	 * @param receiver
	 *            the new composite receiver
	 */
	public void setCompositeReceiver(SCMPCompositeReceiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * Sets the composite sender.
	 * 
	 * @param sender
	 *            the new composite sender
	 */
	public void setCompositeSender(SCMPCompositeSender sender) {
		this.sender = sender;
	}
}