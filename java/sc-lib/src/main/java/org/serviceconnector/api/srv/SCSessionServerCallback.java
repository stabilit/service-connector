/*
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
 */
package org.serviceconnector.api.srv;

import org.serviceconnector.api.SCMessage;

/**
 * The Class SCSessionServerCallback. Abstract class provides basic functions for a session server callback.
 */
public abstract class SCSessionServerCallback {

	/** The SC session server. */
	protected SCSessionServer scSessionServer = null;

	public SCSessionServerCallback(SCSessionServer scSessionServer) {
		this.scSessionServer = scSessionServer;
	}

	/**
	 * Creates the session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in millis
	 * @return the sC message
	 */
	public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
		return message;
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 */
	public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
	}

	/**
	 * Abort session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 */
	public void abortSession(SCMessage message, int operationTimeoutInMillis) {
	}

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutInMillis
	 *            the operation timeout in milliseconds
	 * @return the sC message
	 */
	public abstract SCMessage execute(SCMessage message, int operationTimeoutInMillis);
}
