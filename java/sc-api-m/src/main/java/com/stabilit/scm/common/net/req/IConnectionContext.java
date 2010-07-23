package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.srv.IIdleCallback;

public interface IConnectionContext extends IContext {

	public abstract IConnection getConnection();
	public abstract int getIdleTimeout();
	public abstract int getOperationTimeoutMillis();
	public abstract IIdleCallback getIdleCallback();
}
