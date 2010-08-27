package com.stabilit.scm.common.log;

public interface IConnectionLogger {

	public abstract void logConnect(String source, int port);

	public abstract void logDisconnect(String source, int port);

	public abstract void logRead(String source, int port, byte[] data, int offset, int length);

	public abstract void logWrite(String source, int port, byte[] data, int offset, int length);

	public abstract void logKeepAlive(String source, int nrOfIdles);
}
