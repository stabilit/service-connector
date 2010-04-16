package com.stabilit.sc.common.listener;

public class ConnectionListenerSupport extends ListenerSupport<IConnectionListener> {

	private static ConnectionListenerSupport connectionListenerSupport = new ConnectionListenerSupport();

	private ConnectionListenerSupport() {
	}
	
	public static ConnectionListenerSupport getInstance() {
		return connectionListenerSupport;
	}

	public static void fireWrite(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireWrite(Object source, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer, int offset, int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public void fireWrite(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			connectionListener.writeEvent(connectionEvent);
		}
	}

	public void fireRead(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			connectionListener.readEvent(connectionEvent);
		}
	}
}
