package com.stabilit.sc.context;

import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class SCMPSessionContext extends SessionContext {

	public SCMPSessionContext() {
	}

	public static ISession getSession(SCMP scmp, boolean fCreate) {
		if (scmp == null) {
			return null;
		}
		String sessionId = scmp.getSessionId();
		ISession session = null;
		if (sessionId != null) {
			session = SessionContext.getSession(sessionId);
		}
		if (session != null) {
			return session;
		}
		session = SessionContext.createSession();
		return session;
	}

}
