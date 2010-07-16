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
import com.stabilit.scm.common.scmp.internal.SCMPCompositeSender;

/**
 * The Class SCMPCompositeSenderRegistry.
 */
public final class SCMPCompositeSenderRegistry extends Registry {

	/** The instance. */
	private final static SCMPCompositeSenderRegistry instance = new SCMPCompositeSenderRegistry();

	private SCMPCompositeSenderRegistry() {
	}

	public static SCMPCompositeSenderRegistry getCurrentInstance() {
		return instance;
	}

	public void addSCMPCompositeSender(Object key, SCMPCompositeSender compositeSender) {
		this.put(key, compositeSender);
	}

	public SCMPCompositeSender getSCMPCompositeSender(Object key) {
		return (SCMPCompositeSender) super.get(key);
	}
	
	public void removeSCMPCompositeSender(Object key) {
		super.remove(key);
	}
}
