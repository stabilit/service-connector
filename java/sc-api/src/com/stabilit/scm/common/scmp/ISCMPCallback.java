package com.stabilit.scm.common.scmp;

import com.stabilit.scm.common.ctx.IContext;



public interface ISCMPCallback {

	public abstract void setContext(IContext context);
	
	public abstract void callback(SCMPMessage scmpReply) throws Exception;
	
	public abstract void callback(Throwable th);
	
}
