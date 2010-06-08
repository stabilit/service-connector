/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

/**
 * @author JTraber
 */
public class ServiceConnector implements IServiceConnector {

	private String host;
	private int port;
	private ServiceConnectorContext serviceConnectorCtx;

	public ServiceConnector(String host, int port) {
		this.host = host;
		this.port = port;
		this.serviceConnectorCtx = new ServiceConnectorContext();
		this.serviceConnectorCtx.setAttribute("port", this.port);
		this.serviceConnectorCtx.setAttribute("host", this.host);
	}

	@Override
	public void connect() throws Exception {
		
	}

	@Override
	public ISession createDataSession(String string) {
		return null;
	}

	@Override
	public void disconnect() throws Exception {
	}

	@Override
	public IServiceConnectorContext getSCContext() {
		return serviceConnectorCtx;
	}

	@Override
	public void setAttribute(String string, int i) {
	}
}
