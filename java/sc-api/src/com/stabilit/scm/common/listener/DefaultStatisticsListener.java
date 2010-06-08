package com.stabilit.scm.common.listener;

import com.stabilit.scm.common.scmp.SCMPMessage;

public class DefaultStatisticsListener implements IStatisticsListener {

	private int openConnections;
	private int maxConnections;
	private int openSessions;
	private int maxSessions;
	private int bytesRead;
	private int bytesWritten;
	private int exceptions;
	private int logs;
	private int runtimeWarnings;
	private int encodedSCMPMessage;
	private int encodedSCMPMessageParts;
	private int decodedSCMPMessage;
	private int decodedSCMPMessageParts;

	public DefaultStatisticsListener() {
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
			this.openSessions--;
			break;
		case READ:
			ConnectionEvent connectionEvent = (ConnectionEvent) statisticsEvent
					.getEventObject();
			this.bytesRead += connectionEvent.getLength();
			break;
		case WRITE:
			connectionEvent = (ConnectionEvent) statisticsEvent
					.getEventObject();
			this.bytesWritten += connectionEvent.getLength();
			break;
		case EXCEPTION:
			this.exceptions++;
			break;
		case RUNTIME:
			this.runtimeWarnings++;
			break;
		case LOGGER:
			LoggerEvent loggerEvent = (LoggerEvent) statisticsEvent
					.getEventObject();
			this.logs++;
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

}
