package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IHttpServerConnection;
import com.stabilit.sc.io.SCMP;

public abstract class SCServiceListener implements ISCServiceListener {

	private IHttpServerConnection conn;
	
	//important for instancing by .newInstance() method.
	public SCServiceListener() {
	}

	@Override
	public void messageReceived(IHttpServerConnection conn, SCMP scmp) throws Exception {		
		//TODO callback ?? 
	}

	@Override
	public void setConnection(IHttpServerConnection conn) {		
		this.conn = conn;
	}
}
