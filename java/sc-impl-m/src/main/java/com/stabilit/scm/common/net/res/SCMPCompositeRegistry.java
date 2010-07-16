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
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeReceiver;
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class SCMPCompositeRegistry.
 */
public final class SCMPCompositeRegistry extends Registry {

	/** The instance. */
	private final static SCMPCompositeRegistry instance = new SCMPCompositeRegistry();

	private SCMPCompositeRegistry() {
	}

	public static SCMPCompositeRegistry getCurrentInstance() {
		return instance;
	}

	public void addSCMPCompositeReceiver(Object key, SCMPCompositeReceiver compositeReceiver) {
		this.put(key, compositeReceiver);
	}

	public SCMPCompositeReceiver getSCMPCompositeReceiver(Object key) {
		return (SCMPCompositeReceiver) super.get(key);
	}

	public void removeSCMPCompositeReceiver(Object key) {
		super.remove(key);
	}

	public void addSCMPCompositeSender(Object key, SCMPCompositeSender compositeSender) {
		this.put(key, compositeSender);
	}

	public SCMPCompositeSender getSCMPCompositeSender(Object key) {
		return (SCMPCompositeSender) super.get(key);
	}

	public SCMPMessage getSCMPCompositeComponent(Object key) {
		return (SCMPMessage) super.get(key);
	}

	public void removeSCMPCompositeSender(Object key) {
		super.remove(key);
	}
}
