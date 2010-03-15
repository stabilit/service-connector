package com.stabilit.sc.io;

import com.stabilit.sc.ctx.ISessionContext;

public interface ISession {

	public static final String SESSION_ID = "com.stabilit.sc.io.SESSION_ID";
	
	public String getId();
	
	public ISessionContext getContext();
	
}
