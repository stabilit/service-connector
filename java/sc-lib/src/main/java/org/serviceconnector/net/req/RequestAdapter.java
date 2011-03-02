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
package org.serviceconnector.net.req;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class RequestAdapter. Provides basic functionality for requests.
 * 
 * @author JTraber
 */
public abstract class RequestAdapter implements IRequest {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(RequestAdapter.class);

	/** The scmp message. */
	private SCMPMessage message;
	/** The local socket address. */
	private InetSocketAddress localSocketAddress;
	/** The remote socket address. */
	private InetSocketAddress remoteSocketAddress;

	/**
	 * Instantiates a new request adapter.
	 */
	public RequestAdapter(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		this.localSocketAddress = localAddress;
		this.remoteSocketAddress = remoteAddress;
		this.message = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getMessage() {
		return this.message;
	}

	/** {@inheritDoc} */
	@Override
	public void setMessage(SCMPMessage message) {
		this.message = message;
	}

	/** {@inheritDoc} */
	@Override
	public InetSocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	/** {@inheritDoc} */
	@Override
	public InetSocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() throws Exception {
		SCMPMessage message = this.getMessage();
		if (message == null) {
			return null;
		}
		String messageType = message.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}
}
