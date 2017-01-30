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
package org.serviceconnector.api.cln;

import org.serviceconnector.api.SCAppendMessage;
import org.serviceconnector.api.SCRemovedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SCGuardianCallback. Abstract class provides basic functions for a guardian callback.
 * 
 * @author JTraber
 */
public abstract class SCGuardianMessageCallback extends SCMessageCallback {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCGuardianMessageCallback.class);

	/**
	 * Instantiates a new SCGuardianCallback.
	 */
	public SCGuardianMessageCallback() {
		super(null);
	}

	/**
	 * Receive appendix.
	 * 
	 * @param appendix
	 *            the appendix
	 */
	public abstract void receiveAppendix(SCAppendMessage appendix);

	/**
	 * Receive remove.
	 * 
	 * @param remove
	 *            the remove
	 */
	public abstract void receiveRemove(SCRemovedMessage remove);
}