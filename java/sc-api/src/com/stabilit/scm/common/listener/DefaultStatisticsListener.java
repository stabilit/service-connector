package com.stabilit.scm.common.listener;

public class DefaultStatisticsListener implements IStatisticsListener {

	private int openConnections;
	private int bytesRead;
	private int bytesWritten;
	
	public DefaultStatisticsListener() {
		this.openConnections = 0;
		this.bytesRead = 0;
		this.bytesWritten = 0;
	}

	@Override
	public void connectionStatistics(StatisticsEvent statisticsEvent)
			throws Exception {
		ConnectionEvent connectionEvent = (ConnectionEvent) statisticsEvent.getEventObject();
		switch (statisticsEvent.getEventType()) {
		case CONNECT:
			this.openConnections++;
			break;
		case DISCONNECT:
			this.openConnections--;
			break;
		case READ:
			this.bytesRead += connectionEvent.getLength();
			break;
		case WRITE:
			this.bytesWritten += connectionEvent.getLength();
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
		builder.append(", openConnections=");
		builder.append(openConnections);
		builder.append("]");
		return builder.toString();
	}
	
}
