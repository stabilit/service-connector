package com.stabilit.sc.context;

import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;

public class SCOPSessionContext extends SessionContext {

	public SCOPSessionContext() {
	}

	public static ISession getSession(SCOP scop, boolean fCreate) {
		if (scop == null) {
			return null;
		}
		String sessionId = scop.getSessionId();
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
