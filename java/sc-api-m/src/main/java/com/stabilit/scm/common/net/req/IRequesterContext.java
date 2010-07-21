package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;

public interface IRequesterContext extends IContext {

	public abstract IConnectionPool getConnectionPool();
}
