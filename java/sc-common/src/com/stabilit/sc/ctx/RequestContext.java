package com.stabilit.sc.ctx;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext implements IRequestContext {

	private Map<String, Object> attrMap;

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
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return (SocketAddress) this.getAttribute(SocketAddress.class.getName());
	}
	
	public void setSocketAddress(SocketAddress socketAddress) {
		this.setAttribute(SocketAddress.class.getName(), socketAddress.toString());
	}

}
