package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IHttpServerConnection;
import com.stabilit.sc.io.SCMP;

public interface ISCServiceListener {

	public void messageReceived(IHttpServerConnection conn, SCMP scmp) throws Exception;
	
	public void setConnection(IHttpServerConnection conn);
}
