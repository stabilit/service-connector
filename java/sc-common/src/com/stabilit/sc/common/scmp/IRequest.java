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

public interface IRequest {

	public SCMPMsgType getKey() throws Exception;

	public IRequestContext getContext();
		
	public SCMP getSCMP() throws Exception;
	
	public void setSCMP(SCMP scmp);
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);

	public MapBean<Object> getAttributeMapBean();

	public SocketAddress getSocketAddress();
	
	public void read() throws Exception;
	
	public void readNext() throws Exception;
	
	public void load() throws Exception;		
}
