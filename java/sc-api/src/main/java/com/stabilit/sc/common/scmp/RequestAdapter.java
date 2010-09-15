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
package com.stabilit.sc.common.scmp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class RequestAdapter. Provides basic functionality for requests.
 * 
 * @author JTraber
 */
public abstract class RequestAdapter implements IRequest {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(RequestAdapter.class);
	
	/** The scmp message. */
	private SCMPMessage message;
	/** The map bean. MapBean to store any data. */
	private Map<String, Object> attrMap;
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
		this.attrMap = new HashMap<String, Object>();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getMessage() throws Exception {
		if (this.message == null) {
			load();
		}
		return this.message;
	}

	/** {@inheritDoc} */
	@Override
	public void setMessage(SCMPMessage message) {
		this.message = message;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String key) {
		// looks up the attribute in request map and in received message map
		if (this.attrMap.containsKey(key)) {
			return this.attrMap.get(key);
		}
		return this.message.getHeader(key);
	}

	@Override
	public Object getAttribute(SCMPHeaderAttributeKey key) {
		return this.getAttribute(key.getValue());
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String key, Object value) {
		this.attrMap.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(SCMPHeaderAttributeKey key, Object value) {
		this.setAttribute(key.getValue(), value);
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
	public void read() throws Exception {
		if (message == null) {
			load();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void readNext() throws Exception {
		this.message = null;
		read();
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

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.message + " Attributes: " + this.attrMap;
	}
}
