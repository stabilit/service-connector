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
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;

/**
 * The Class RequesterContext.
 * 
 * @author JTraber
 */
public class RequesterContext {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(RequesterContext.class);
	protected SCMPMessageSequenceNr msgSequenceNr;

	private String host;
	private int port;
	private String connectionType;
	private int keepAliveIntervalInSeconds;
	private int maxConnections;

	public RequesterContext(String host, int port, String connectionType, int keepAliveIntervalInSeconds) {
		this(host, port, connectionType, keepAliveIntervalInSeconds, Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE);
	}

	public RequesterContext(String host, int port, String connectionType, int keepAliveIntervalInSeconds, int maxConnections) {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.maxConnections = maxConnections;
		this.msgSequenceNr = new SCMPMessageSequenceNr();
	}

	/**
	 * Gets the SCMP msg sequence nr.
	 * 
	 * @return the SCMP msg sequence nr
	 */
	public SCMPMessageSequenceNr getSCMPMsgSequenceNr() {
		return this.msgSequenceNr;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public int getKeepAliveIntervalInSeconds() {
		return keepAliveIntervalInSeconds;
	}

	public int getMaxConnections() {
		return maxConnections;
	}
}
