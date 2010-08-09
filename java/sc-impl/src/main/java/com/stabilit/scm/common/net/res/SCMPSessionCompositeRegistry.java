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
package com.stabilit.scm.common.net.res;

import com.stabilit.scm.common.registry.Registry;
import com.stabilit.scm.common.scmp.SCMPMessageId;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class SCMPSessionCompositeRegistry. Stores composite components of a communication to resume at the time it gets
 * active again.
 * 
 * @author JTraber
 */
public final class SCMPSessionCompositeRegistry extends Registry<String, SCMPSessionCompositeItem> {

	/** The instance. */
	private final static SCMPSessionCompositeRegistry instance = new SCMPSessionCompositeRegistry();

	/**
	 * Instantiates a new sCMP session composite registry.
	 */
	private SCMPSessionCompositeRegistry() {
	}

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static SCMPSessionCompositeRegistry getCurrentInstance() {
		return instance;
	}

	/**
	 * Adds the session.
	 * 
	 * @param key
	 *            the key
	 */
	public void addSession(String key) {
		this.put(key, new SCMPSessionCompositeItem());
	}

	/**
	 * Removes the session.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSession(String key) {
		super.remove(key);
	}

	/**
	 * Adds the scmp composite receiver.
	 * 
	 * @param key
	 *            the key
	 * @param compositeReceiver
	 *            the composite receiver
	 */
	public void addSCMPCompositeReceiver(String key, SCMPCompositeReceiver compositeReceiver) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeReceiver(compositeReceiver);
	}

	/**
	 * Gets the sCMP composite receiver.
	 * 
	 * @param key
	 *            the key
	 * @return the sCMP composite receiver
	 */
	public SCMPCompositeReceiver getSCMPCompositeReceiver(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getCompositeReceiver();
	}

	/**
	 * Removes the scmp composite receiver.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSCMPCompositeReceiver(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeReceiver(null);
	}

	/**
	 * Adds the scmp composite sender.
	 * 
	 * @param key
	 *            the key
	 * @param compositeSender
	 *            the composite sender
	 */
	public void addSCMPCompositeSender(String key, SCMPCompositeSender compositeSender) {
		SCMPSessionCompositeItem item = this.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeSender(compositeSender);
	}

	/**
	 * Gets the sCMP composite sender.
	 * 
	 * @param key
	 *            the key
	 * @return the sCMP composite sender
	 */
	public SCMPCompositeSender getSCMPCompositeSender(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getCompositeSender();
	}

	/**
	 * Removes the scmp composite sender.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeSCMPCompositeSender(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeSender(null);
	}

	/**
	 * Gets the sCMP message id.
	 * 
	 * @param key
	 *            the key
	 * @return the sCMP message id
	 */
	public SCMPMessageId getSCMPMessageId(String key) {
		SCMPSessionCompositeItem item = super.get(key);
		if (item == null) {
			return null;
		}
		return item.getMessageId();
	}
}
