package com.stabilit.sc.ctx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.Session;

public class SessionContext implements ISessionContext {

	private static Map<String, ISession> sessionMap = new ConcurrentHashMap<String, ISession>();
		
	private Map<String, Object> attrMap;

	public SessionContext() {
		this.attrMap = new ConcurrentHashMap<String, Object>();
	}
		
	public static ISession getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public static ISession createSession() {
		Session session = new Session();
		sessionMap.put(session.getId(), session);
		return session;
	}
	
	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

}
