package com.stabilit.sc.common;

import com.stabilit.sc.common.factory.IFactoryable;

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
