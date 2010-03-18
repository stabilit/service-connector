package com.stabilit.sc.io;

import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.msg.MsgType;

public interface IRequest {

	public MsgType getKey();

	public IRequestContext getContext();
	
	public ISession getSession(boolean fCreate);
	
	public SCMP getSCMP();
		
}
