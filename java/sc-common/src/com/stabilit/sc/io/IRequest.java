package com.stabilit.sc.io;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.util.MapBean;

public interface IRequest {

	public SCMPMsgType getKey();

	public IRequestContext getContext();
	
	public ISession getSession(boolean fCreate);
	
	public SCMP getSCMP();
	
	public void setAttribute(String key, Object value);
	
	public Object getAttribute(String key);

	public MapBean<Object> getAttributeMapBean();
		
}
