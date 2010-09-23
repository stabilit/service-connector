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
package org.serviceconnector.net.req;

import org.apache.log4j.Logger;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.req.IRequesterContext;
import org.serviceconnector.scmp.SCMPMessageId;


/**
 * @author JTraber
 */
public class RequesterContext implements IRequesterContext {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RequesterContext.class);
	
	private ConnectionPool connectionPool;
	private SCMPMessageId msgId;

	public RequesterContext(ConnectionPool connectionPool, SCMPMessageId msgId) {
		this.connectionPool = connectionPool;
		this.msgId = msgId;
	}

	@Override
	public ConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

	@Override
	public SCMPMessageId getSCMPMessageId() {
		return this.msgId;
	}
}
