package com.stabilit.sc.common.io;

import java.net.SocketAddress;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.util.MapBean;

public interface IRequest {

	public SCMPMsgType getKey() throws Exception;

	public IRequestContext getContext();
		
	public SCMP getSCMP() throws Exception;
	
	public void setSCMP(SCMP scmp);
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);

	public MapBean<Object> getAttributeMapBean();

	public SocketAddress getSocketAddress();
	
	public void read() throws Exception;
		
}
