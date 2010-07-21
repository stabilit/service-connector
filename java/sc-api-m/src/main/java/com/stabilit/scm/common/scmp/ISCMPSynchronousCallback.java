package com.stabilit.scm.common.scmp;


public interface ISCMPSynchronousCallback extends ISCMPCallback {

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

	public abstract SCMPMessage getMessageSync(int timeoutInMillis) throws Exception;

}
