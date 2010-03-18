package com.stabilit.sc.client;

import com.stabilit.sc.factory.IFactoryable;

public interface IConnection extends IFactoryable {	
	
	/**
	 * @param host
	 */
	public void setHost(String host);

	/**
	 * @param port
	 */
	public void setPort(int port);
}
