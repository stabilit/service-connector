package com.stabilit.sc.msg;

import com.stabilit.sc.app.server.IHTTPServerConnection;
import com.stabilit.sc.io.SCMP;

public interface ISCServiceListener {

	public void messageReceived(IHTTPServerConnection conn, SCMP scmp) throws Exception;
	
	public void setConnection(IHTTPServerConnection conn);
}
