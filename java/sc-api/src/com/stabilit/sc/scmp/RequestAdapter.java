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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getSCMP()
	 */
	@Override
	public SCMPMessage getMessage() throws Exception {
		if (message == null) {
			load();
		}
		return message;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#setSCMP(com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public void setMessage(SCMPMessage message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getContext()
	 */
	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String key) {
		return mapBean.getAttribute(key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String key, Object value) {
		mapBean.setAttribute(key, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getAttributeMapBean()
	 */
	@Override
	public MapBean<Object> getAttributeMapBean() {
		return mapBean;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getSocketAddress()
	 */
	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#read()
	 */
	@Override
	public void read() throws Exception {
		if (message == null) {
			load();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#readNext()
	 */
	@Override
	public void readNext() throws Exception {
		this.message = null;
		read();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IRequest#getKey()
	 */
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
