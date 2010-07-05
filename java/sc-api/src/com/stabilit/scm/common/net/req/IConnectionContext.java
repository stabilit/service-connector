package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;

public interface IConnectionContext extends IContext {

	@Override
	public abstract IConnectionPool getConnectionPool();
	public abstract IConnection getConnection();
	public abstract void setOuterContext(IContext outerContext);
	public abstract IContext getOuterContext();
	public abstract int getIdleTimeout();
	public abstract int getReadTimeout();
	public abstract int getWriteTimeout();
}
