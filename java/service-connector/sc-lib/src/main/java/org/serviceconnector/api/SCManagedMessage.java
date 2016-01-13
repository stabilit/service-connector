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
package org.serviceconnector.api;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SCManagedMessage. Message which is managed by the cache module in SC.
 */
public class SCManagedMessage extends SCMessage {

	/** The appendixes. */
	private List<SCAppendMessage> appendixes;

	/**
	 * Instantiates a new SC managed message.
	 */
	public SCManagedMessage() {
		this.appendixes = new ArrayList<SCAppendMessage>();
	}

	/**
	 * Gets the appendixes.
	 * 
	 * @return the appendixes
	 */
	public List<SCAppendMessage> getAppendixes() {
		return appendixes;
	}

	/**
	 * Adds the appendix.
	 * 
	 * @param appendix
	 *            the appendix
	 */
	public void addAppendix(SCAppendMessage appendix) {
		this.appendixes.add(appendix);
	}

	/**
	 * Gets the number of appendixes.
	 * 
	 * @return the number of appendixes
	 */
	public int getNrOfAppendixes() {
		return this.appendixes.size();
	}

	/**
	 * Checks if is managed.
	 * 
	 * @return true, if is managed {@inheritDoc}
	 */
	@Override
	public boolean isManaged() {
		return true;
	}
}
