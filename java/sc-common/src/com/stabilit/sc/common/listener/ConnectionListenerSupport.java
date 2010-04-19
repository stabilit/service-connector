package com.stabilit.sc.common.listener;

public class ConnectionListenerSupport extends
		ListenerSupport<IConnectionListener> {

	private static ConnectionListenerSupport connectionListenerSupport = new ConnectionListenerSupport();

	private ConnectionListenerSupport() {
	}

	public static ConnectionListenerSupport getInstance() {
		return connectionListenerSupport;
	}

	public static void fireConnect(Object source) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, null);
			ConnectionListenerSupport.getInstance().fireConnect(connectionEvent);
		}
	}

	public static void fireDisconnect(Object source) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source, null);
			ConnectionListenerSupport.getInstance().fireDisconnect(connectionEvent);
		}
	}

	public static void fireWrite(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source,
					buffer);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireWrite(Object source, byte[] buffer, int offset,
			int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source,
					buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireWrite(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source,
					buffer);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public static void fireRead(Object source, byte[] buffer, int offset,
			int length) {
		if (getInstance().isEmpty() == false) {
			ConnectionEvent connectionEvent = new ConnectionEvent(source,
					buffer, offset, length);
			ConnectionListenerSupport.getInstance().fireRead(connectionEvent);
		}
	}

	public void fireConnect(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			try {
				connectionListener.connectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireDisconnect(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			try {
				connectionListener.disconnectEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireWrite(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			try {
				connectionListener.writeEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fireRead(ConnectionEvent connectionEvent) {
		for (IConnectionListener connectionListener : listenerList) {
			try {
				connectionListener.readEvent(connectionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
