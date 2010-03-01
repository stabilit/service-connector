package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IServerConnection;
import com.stabilit.sc.io.SCMP;

public interface ISCServiceListener {

	public void messageReceived(IServerConnection conn, SCMP scmp) throws Exception;
	
	public void setConnection(IServerConnection conn);
}
