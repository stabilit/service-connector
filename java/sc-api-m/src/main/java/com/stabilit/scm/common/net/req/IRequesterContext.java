package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.scmp.SCMPMessageId;

public interface IRequesterContext extends IContext {

	public abstract IConnectionPool getConnectionPool();

	public abstract SCMPMessageId getSCMPMessageId();
}
