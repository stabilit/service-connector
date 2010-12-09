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
package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.SynchronousCallback;

/**
 * The Class CommandCallback. CommandCallback might be used in a command if executing the command jobs needs a callback.
 * Error handling is addressed by this callback.
 */
public class CommandCallback extends SynchronousCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommandCallback.class);

	/** The Constant ERROR_STRING. */
	protected static final String ERROR_STRING_TIMEOUT = "Operation timeout expired on SC";
	/** The Constant ERROR_STRING_CONNECTION. */
	protected static final String ERROR_STRING_CONNECTION = "broken connection";
	/** The Constant ERROR_STRING_FAIL. */
	protected static final String ERROR_STRING_FAIL = "executing command failed";

	public CommandCallback() {
		super();
	}

	public CommandCallback(boolean synchronous) {
		this.synchronous = synchronous;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT_EXPIRED, ERROR_STRING_TIMEOUT);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, ERROR_STRING_CONNECTION);
		} else if (ex instanceof ConnectionPoolBusyException) {
			fault = new SCMPMessageFault(ex, SCMPError.SC_ERROR, ERROR_STRING_FAIL);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, ERROR_STRING_FAIL);
		}
		super.callback(fault);
	}
}
