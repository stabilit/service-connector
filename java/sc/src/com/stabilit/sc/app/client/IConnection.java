package com.stabilit.sc.app.client;

import com.stabilit.sc.io.SCMP;

public interface IConnection {

	void send(SCMP scmp) throws Exception;

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	public void destroy() throws Exception;
}
