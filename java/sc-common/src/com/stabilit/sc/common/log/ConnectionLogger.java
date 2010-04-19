package com.stabilit.sc.common.log;

import java.io.IOException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.listener.ConnectionEvent;
import com.stabilit.sc.common.listener.IConnectionListener;

public class ConnectionLogger extends SimpleLogger implements IConnectionListener {

	public ConnectionLogger() throws Exception {
		this("log/", "con.log");
	}

	public ConnectionLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}

	public void log(byte[] buffer) throws IOException {
		super.log(buffer);
	}

	public void log(byte[] buffer, int offset, int length) throws IOException {
		super.log(buffer, offset,length);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	@Override
	public void connectEvent(ConnectionEvent connectionEvent) {
		try {
			this.log("------- connect -------\r\n");
			this.log("connect by class " + connectionEvent.getSource().getClass().getName());
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnectEvent(ConnectionEvent connectionEvent) {
		try {
			this.log("------- disconnect -------\r\n");
			this.log("disconnect by class " + connectionEvent.getSource().getClass().getName());
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.log(">>>>>>>> read >>>>>>>\r\n");
			if (length > 0) {
			    this.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
			    this.log((byte[]) connectionEvent.getData());			
			}
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeEvent(ConnectionEvent connectionEvent) {
		try {
			int length = connectionEvent.getLength();
			this.log("<<<<<<<< write <<<<<<<<\r\n");
			if (length > 0) {
			    this.log((byte[]) connectionEvent.getData(), connectionEvent.getOffset(), length);
			} else {
			    this.log((byte[]) connectionEvent.getData());			
			}
			this.log("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
