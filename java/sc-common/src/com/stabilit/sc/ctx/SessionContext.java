package com.stabilit.sc.ctx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.Session;

public class SessionContext extends ContextAdapter implements ISessionContext {

	private static Map<String, ISession> sessionMap = new ConcurrentHashMap<String, ISession>();
		
	public SessionContext() {
		super();
	}
		
	public static ISession getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public static ISession createSession() {
		Session session = new Session();
		sessionMap.put(session.getId(), session);
		return session;
	}
	
}
