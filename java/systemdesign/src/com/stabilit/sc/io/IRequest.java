package com.stabilit.sc.io;

import com.stabilit.sc.context.IRequestContext;

public interface IRequest {

	public String getKey();

	public IRequestContext getContext();
	
	public ISession getSession(boolean fCreate);
	
	public SCMP getSCMP();
		
}
