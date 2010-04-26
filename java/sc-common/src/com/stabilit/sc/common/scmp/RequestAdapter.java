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

import java.net.SocketAddress;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.util.MapBean;


/**
 * @author JTraber
 * 
 */
public abstract class RequestAdapter implements IRequest {
	protected SCMP scmp;
	protected MapBean<Object> mapBean;
	protected SocketAddress socketAddress;
	protected IRequestContext requestContext;
	
	public RequestAdapter() {
		this.scmp = null;
	}

	@Override
	public SCMP getSCMP() throws Exception {
		if (scmp == null) {
			load();
		}
		return scmp;
	}

	@Override
	public void setSCMP(SCMP scmp) {
		this.scmp = scmp;
	}

	@Override
	public IRequestContext getContext() {
		return requestContext;
	}

	@Override
	public Object getAttribute(String key) {
		return mapBean.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		mapBean.setAttribute(key, value);
	}

	@Override
	public MapBean<Object> getAttributeMapBean() {
		return mapBean;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	@Override
	public void read() throws Exception {
		if (scmp == null) {
			load();
		}
	}
	
	@Override
	public SCMPMsgType getKey() throws Exception {
		SCMP scmp = this.getSCMP();
		if (scmp == null) {
			return null;
		}
		String messageType = scmp.getMessageType();
		return SCMPMsgType.getMsgType(messageType);
	}
}
