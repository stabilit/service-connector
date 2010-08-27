package com.stabilit.scm.common.log;

import com.stabilit.scm.common.scmp.SCMPMessage;

public interface ISubscriptionLogger {

	public abstract void logNoDataTimeout(String source, String sessionId);

	public abstract void logPoll(String source, String sessionId, SCMPMessage queueMessage, int queueSize);

	public abstract void logAdd(String source, SCMPMessage queueMessage, int queueSize);

	public abstract void logRemove(String source, int queueSize);
}
