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
package com.stabilit.scm.common.listener;

import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The listener interface for receiving defaultStatistics events. The class that is interested in processing a
 * defaultStatistics event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addDefaultStatisticsListener<code> method. When
 * the defaultStatistics event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see DefaultStatisticsEvent
 */
public class DefaultStatisticsListener implements IStatisticsListener {

	/** The open connections. */
	private int openConnections;
	/** The max connections. */
	private int maxConnections;
	/** The open sessions. */
	private int openSessions;
	/** The max sessions. */
	private int maxSessions;
	/** The bytes read. */
	private int bytesRead;
	/** The bytes written. */
	private int bytesWritten;
	/** The exceptions. */
	private int exceptions;
	/** The keep alive. */
	private int keepAlives;
	/** The logs. */
	private int logs;
	/** The runtime warnings. */
	private int runtimeWarnings;
	/** The encoded scmp message. */
	private int encodedSCMPMessage;
	/** The encoded scmp message parts. */
	private int encodedSCMPMessageParts;
	/** The decoded scmp message. */
	private int decodedSCMPMessage;
	/** The decoded scmp message parts. */
	private int decodedSCMPMessageParts;

	/**
	 * Instantiates a new default statistics listener.
	 */
	public DefaultStatisticsListener() {
		this.clearAll();
	}

	/** {@inheritDoc} */
	@Override
	public void statistics(StatisticsEvent statisticsEvent) throws Exception {
		switch (statisticsEvent.getEventType()) {
		case CONNECT:
			this.openConnections++;
			this.maxConnections++;
			break;
		case DISCONNECT:
			this.openConnections--;
			break;
		case CREATE_SESSION:
			this.openSessions++;
			this.maxSessions++;
			break;
		case DELETE_SESSION:
		case ABORT_SESSION:
			this.openSessions--;
			break;
		case READ:
			ConnectionEvent connectionEvent = (ConnectionEvent) statisticsEvent.getEventObject();
			this.bytesRead += connectionEvent.getLength();
			break;
		case WRITE:
			connectionEvent = (ConnectionEvent) statisticsEvent.getEventObject();
			this.bytesWritten += connectionEvent.getLength();
			break;
		case EXCEPTION:
			this.exceptions++;
			break;
		case RUNTIME:
			this.runtimeWarnings++;
			break;
		case LOGGER:
			// LoggerEvent loggerEvent = (LoggerEvent) statisticsEvent.getEventObject();
			this.logs++;
			break;
		case KEEP_ALIVE:
			this.keepAlives++;
			break;
		case ENCODE_SCMP:
			SCMPEvent scmpEvent = (SCMPEvent) statisticsEvent.getEventObject();
			SCMPMessage scmp = scmpEvent.getSCMP();
			if (scmp.isPart()) {
				this.encodedSCMPMessageParts++;
			} else {
				this.encodedSCMPMessage++;
			}
			break;
		case DECODE_SCMP:
			scmpEvent = (SCMPEvent) statisticsEvent.getEventObject();
			scmp = scmpEvent.getSCMP();
			if (scmp.isPart()) {
				this.decodedSCMPMessageParts++;
			} else {
				this.decodedSCMPMessage++;
			}
			break;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultStatisticsListener [bytesRead=");
		builder.append(bytesRead);
		builder.append(", bytesWritten=");
		builder.append(bytesWritten);
		builder.append(", decodedSCMPMessage=");
		builder.append(decodedSCMPMessage);
		builder.append(", decodedSCMPMessageParts=");
		builder.append(decodedSCMPMessageParts);
		builder.append(", encodedSCMPMessage=");
		builder.append(encodedSCMPMessage);
		builder.append(", encodedSCMPMessageParts=");
		builder.append(encodedSCMPMessageParts);
		builder.append(", exceptions=");
		builder.append(exceptions);
		builder.append(", logs=");
		builder.append(logs);
		builder.append(", keepAlives=");
		builder.append(keepAlives);
		builder.append(", maxConnections=");
		builder.append(maxConnections);
		builder.append(", maxSessions=");
		builder.append(maxSessions);
		builder.append(", openConnections=");
		builder.append(openConnections);
		builder.append(", openSessions=");
		builder.append(openSessions);
		builder.append(", runtimeWarnings=");
		builder.append(runtimeWarnings);
		builder.append("]");
		return builder.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void clearAll() {
		this.openConnections = 0;
		this.maxConnections = 0;
		this.openSessions = 0;
		this.maxSessions = 0;
		this.bytesRead = 0;
		this.bytesWritten = 0;
		this.exceptions = 0;
		this.logs = 0;
		this.runtimeWarnings = 0;
		this.encodedSCMPMessage = 0;
		this.encodedSCMPMessageParts = 0;
		this.decodedSCMPMessage = 0;
		this.decodedSCMPMessageParts = 0;
	}
}
