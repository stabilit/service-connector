package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;

public interface IConnectionContext extends IContext {

	@Override
	public abstract IConnectionPool getConnectionPool();
	public abstract IConnection getConnection();
	public abstract IContext getOuterContext();
}
