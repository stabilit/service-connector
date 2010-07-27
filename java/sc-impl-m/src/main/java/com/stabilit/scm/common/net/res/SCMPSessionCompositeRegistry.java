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
 * The Class SCMPSessionCompositeRegistry.
 */
public final class SCMPSessionCompositeRegistry extends Registry {

	/** The instance. */
	private final static SCMPSessionCompositeRegistry instance = new SCMPSessionCompositeRegistry();

	private SCMPSessionCompositeRegistry() {
	}

	public static SCMPSessionCompositeRegistry getCurrentInstance() {
		return instance;
	}

	public void addSession(Object key) {
		this.put(key, new SCMPSessionCompositeItem());
	}

	public void removeSession(Object key) {
		super.remove(key);
	}

	public void addSCMPCompositeReceiver(Object key, SCMPCompositeReceiver compositeReceiver) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) this.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeReceiver(compositeReceiver);
	}

	public SCMPCompositeReceiver getSCMPCompositeReceiver(Object key) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) super.get(key);
		if (item == null) {
			return null;
		}
		return item.getCompositeReceiver();
	}

	public void removeSCMPCompositeReceiver(Object key) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) super.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeReceiver(null);
	}

	public void addSCMPCompositeSender(Object key, SCMPCompositeSender compositeSender) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) this.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeSender(compositeSender);
	}

	public SCMPCompositeSender getSCMPCompositeSender(Object key) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) super.get(key);
		if (item == null) {
			return null;
		}
		return item.getCompositeSender();
	}

	public void removeSCMPCompositeSender(Object key) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) super.get(key);
		if (item == null) {
			return;
		}
		item.setCompositeSender(null);
	}

	public SCMPMessageId getSCMPMessageId(Object key) {
		SCMPSessionCompositeItem item = (SCMPSessionCompositeItem) super.get(key);
		if (item == null) {
			return null;
		}
		return item.getMessageId();
	}

	private class SCMPSessionCompositeItem {
		private SCMPCompositeReceiver receiver;
		private SCMPCompositeSender sender;
		private SCMPMessageId messageId;

		public SCMPSessionCompositeItem() {
			this(null, null);
		}

		public SCMPSessionCompositeItem(SCMPCompositeReceiver receiver, SCMPCompositeSender sender) {
			super();
			this.receiver = receiver;
			this.sender = sender;
			this.messageId = new SCMPMessageId();
		}

		public SCMPCompositeReceiver getCompositeReceiver() {
			return receiver;
		}

		public SCMPCompositeSender getCompositeSender() {
			return sender;
		}

		public SCMPMessageId getMessageId() {
			return messageId;
		}

		public void setCompositeReceiver(SCMPCompositeReceiver receiver) {
			this.receiver = receiver;
		}

		public void setCompositeSender(SCMPCompositeSender sender) {
			this.sender = sender;
		}
	}
}
