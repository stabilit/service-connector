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
package com.stabilit.sc.srv.server;

/**
 * The Class ServerConnectionAdapter. Provides basic functionality for server connections.
 * 
 * @author JTraber
 */
public abstract class ServerConnectionAdapter implements IServerConnection {

	/** The server. */
	protected IServer server;

	/**
	 * Instantiates a new server connection adapter.
	 */
	public ServerConnectionAdapter() {
		this.server = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#getServer()
	 */
	@Override
	public IServer getServer() {
		return server;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.server.IServerConnection#setServer(com.stabilit.sc.srv.server.IServer)
	 */
	public void setServer(IServer server) {
		this.server = server;
	}
}
