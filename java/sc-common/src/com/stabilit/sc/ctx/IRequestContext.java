package com.stabilit.sc.ctx;

import java.net.SocketAddress;

public interface IRequestContext extends IContext {

	public SocketAddress getSocketAddress();
	
}
