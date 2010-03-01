package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IServerConnection;
import com.stabilit.sc.io.SCMP;

public abstract class SCServiceListener implements ISCServiceListener {

	private IServerConnection conn;
	
	//important for instancing by .newInstance() method.
	public SCServiceListener() {
	}

	@Override
	public void messageReceived(IServerConnection conn, SCMP scmp) throws Exception {		
		//TODO callback ?? 
	}

	@Override
	public void setConnection(IServerConnection conn) {		
		this.conn = conn;
	}
}
