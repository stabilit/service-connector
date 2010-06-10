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
package com.stabilit.scm.common.scmp;

import java.net.SocketAddress;

import com.stabilit.scm.common.ctx.IRequestContext;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.util.MapBean;

/**
 * The Class RequestAdapter. Provides basic functionality for requests.
 * 
 * @author JTraber
 */
public abstract class RequestAdapter implements IRequest {

	/** The scmp message. */
	protected SCMPMessage message;
	/** The map bean. MapBean to store any data. */
	protected MapBean<Object> mapBean;
	/** The local socket address. */
	protected SocketAddress localSocketAddress;
	/** The remote socket address. */
	protected SocketAddress remoteSocketAddress;
	/** The request context. */
	protected IRequestContext requestContext;

	/**
	 * Instantiates a new request adapter.
	 */
	public RequestAdapter(SocketAddress localAddress, SocketAddress remoteAddress) {
		this.localSocketAddress = localAddress;
		this.remoteSocketAddress = remoteAddress;
		this.message = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getSCMP() {
		if (message == null) {
			try {
				load();
			} catch (Exception e) {
				ExceptionPoint.getInstance().fireException(this, e);
				return null;
			}
		}
		return message;
	}

	/** {@inheritDoc} */
	@Override
	public void setMessage(SCMPMessage message) {
		this.message = message;
	}

	/** {@inheritDoc} */
	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String key) {
		// looks up the attribute in request map and in received message map
		if (this.mapBean.containsKey(key)) {
			return this.mapBean.getAttribute(key);
		}
		return this.message.getHeader(key);
	}

	@Override
	public Object getAttribute(SCMPHeaderAttributeKey key) {
		return this.getAttribute(key.getName());
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String key, Object value) {
		mapBean.setAttribute(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public SocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	/** {@inheritDoc} */
	@Override
	public SocketAddress getRemoteSocketAddress() {
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
		SCMPMessage message = this.getSCMP();
		if (message == null) {
			return null;
		}
		String messageType = message.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}
}
