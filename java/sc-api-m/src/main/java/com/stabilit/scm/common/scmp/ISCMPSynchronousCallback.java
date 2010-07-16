package com.stabilit.scm.common.scmp;

import com.stabilit.scm.common.ctx.IContext;

public interface ISCMPSynchronousCallback extends ISCMPCallback {

	@Override
	public abstract IContext getContext();

	@Override
	public abstract void setContext(IContext context);

	@Override
	public abstract void callback(SCMPMessage scmpReply) throws Exception;

	@Override
	public abstract void callback(Throwable th);

	/**
	 * Gets the message synchronous. Waits until message received.
	 * 
	 * @return the message sync
	 * @throws Exception
	 *             the exception
	 */
	public abstract SCMPMessage getMessageSync() throws Exception;

}
