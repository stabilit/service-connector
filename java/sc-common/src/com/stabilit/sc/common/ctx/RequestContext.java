package com.stabilit.sc.common.ctx;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext extends ContextAdapter implements IRequestContext {

	public RequestContext() {
		this.attrMap = new ConcurrentHashMap<String, Object>();
	}
	    
	/**
	 * @param remoteAddress
	 */
	public RequestContext(SocketAddress remoteAddress) {
		this();
		this.setSocketAddress(remoteAddress);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return (SocketAddress) this.getAttribute(SocketAddress.class.getName());
	}
	
	public void setSocketAddress(SocketAddress socketAddress) {
		this.setAttribute(SocketAddress.class.getName(), socketAddress);
	}

}
