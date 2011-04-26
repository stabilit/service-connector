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
package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;

/**
 * The Class SCSessionServerCallback. Abstract class provides basic functions for a session server callback.
 */
public abstract class SCSessionServerCallback implements ISCSessionServerCallback {

	/** The SC session server. */
	protected SCSessionServer scSessionServer = null;

	/**
	 * Instantiates a new sC session server callback.
	 * 
	 * @param scSessionServer
	 *            the sc session server
	 */
	public SCSessionServerCallback(SCSessionServer scSessionServer) {
		this.scSessionServer = scSessionServer;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCSessionServerCallback#createSession(org.serviceconnector.api.SCMessage, int)
	 */
	@Override
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCSessionServerCallback#deleteSession(org.serviceconnector.api.SCMessage, int)
	 */
	@Override
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCSessionServerCallback#abortSession(org.serviceconnector.api.SCMessage, int)
	 */
	@Override
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCSessionServerCallback#execute(org.serviceconnector.api.SCMessage, int)
	 */
	@Override
	public abstract SCMessage execute(SCMessage message, int operationTimeoutMillis);
}
