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
package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.util.SynchronousCallback;

/**
 * The Class CommandCallback. CommandCallback might be used in a command if executing the command jobs needs a callback.
 * Error handling is addressed by this callback.
 */
public class CommandCallback extends SynchronousCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CommandCallback.class);

	/**
	 * Instantiates a new command callback.
	 * 
	 * @param synchronous
	 *            the synchronous
	 */
	public CommandCallback(boolean synchronous) {
		this.synchronous = synchronous;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		LOGGER.warn(ex);
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection");
		} else if (ex instanceof ConnectionPoolBusyException) {
			fault = new SCMPMessageFault(ex, SCMPError.NO_FREE_CONNECTION);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing command failed");
		}
		super.receive(fault);
	}
}
