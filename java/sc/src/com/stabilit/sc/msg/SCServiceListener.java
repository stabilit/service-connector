package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IHTTPServerConnection;
import com.stabilit.sc.io.SCMP;

public abstract class SCServiceListener implements ISCServiceListener {

	private IHTTPServerConnection conn;
	
	//important for instancing by .newInstance() method.
	public SCServiceListener() {
	}

	@Override
	public void messageReceived(IHTTPServerConnection conn, SCMP scmp) throws Exception {		
		//TODO callback ?? 
	}

	@Override
	public void setConnection(IHTTPServerConnection conn) {		
		this.conn = conn;
	}
}
