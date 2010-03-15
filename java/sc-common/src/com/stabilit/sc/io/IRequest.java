package com.stabilit.sc.io;

import com.stabilit.sc.ctx.IRequestContext;

public interface IRequest {

	public String getKey();

	public IRequestContext getContext();
	
	public ISession getSession(boolean fCreate);
	
	public SCMP getSCMP();
		
}
