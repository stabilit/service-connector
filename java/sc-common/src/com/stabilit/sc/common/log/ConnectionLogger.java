package com.stabilit.sc.common.log;

import java.io.IOException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ConnectionEvent;
import com.stabilit.sc.common.listener.IConnectionListener;

public class ConnectionLogger extends SimpleLogger implements IConnectionListener {

	public ConnectionLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	@Override
	public void readEvent(ConnectionEvent connectionEvent) {
		
	}

	@Override
	public void writeEvent(ConnectionEvent connectionEvent) {
		
	}

}
