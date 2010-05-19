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
package com.stabilit.sc.scmp;

import java.net.SocketAddress;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.listener.ExceptionPoint;
import com.stabilit.sc.util.MapBean;

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
	/** The socket address. */
	protected SocketAddress socketAddress;
	/** The request context. */
	protected IRequestContext requestContext;

	/**
	 * Instantiates a new request adapter.
	 */
	public RequestAdapter() {
		this.message = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getMessage() {
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
		return mapBean.getAttribute(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String key, Object value) {
		mapBean.setAttribute(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public MapBean<Object> getAttributeMapBean() {
		return mapBean;
	}

	/** {@inheritDoc} */
	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
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
}
