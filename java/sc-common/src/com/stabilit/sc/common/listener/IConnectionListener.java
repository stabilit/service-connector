package com.stabilit.sc.common.listener;

import java.util.EventListener;

public interface IConnectionListener extends EventListener {

	public void writeEvent(ConnectionEvent connectionEvent);
	
	public void readEvent(ConnectionEvent connectionEvent);
}
