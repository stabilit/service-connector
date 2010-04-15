package com.stabilit.sc.common.listener;

public class ConnectionListenerSupport extends	ListenerSupport<IConnectionListener> {

	private static ConnectionListenerSupport connectionListenerSupport = new ConnectionListenerSupport();

	public static ConnectionListenerSupport getInstance(){
		return connectionListenerSupport;
	}
	
	private ConnectionListenerSupport() {
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
