package com.stabilit.sc.common.ctx;

import java.net.SocketAddress;

public interface IRequestContext extends IContext {

	public SocketAddress getSocketAddress();
		
}
