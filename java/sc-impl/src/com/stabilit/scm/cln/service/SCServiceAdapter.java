/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln.service;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.cln.req.IServiceSession;

/**
 * @author JTraber
 *
 */
public abstract class SCServiceAdapter implements IService {

	protected IRequester client;
	protected IServiceSession session;
	protected Object data;
	
	@Override
	public void destroyService() throws Exception {
		this.session.deleteSession();

		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(client);
		detachCall.invoke();

		this.client.disconnect(); // physical disconnect
		this.client.destroy();
	}

	@Override
	public IServiceContext getServiceContext() {
		return null;
	}

	@Override
	public void setData(Object obj) {
		this.data = obj;
	}
	
	@Override
	public void setRequestor(IRequester client) {
		this.client = client;
	}
	
	@Override
	public void setSession(IServiceSession session) {
		this.session = session;
	}

}
