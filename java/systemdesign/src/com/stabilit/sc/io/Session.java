package com.stabilit.sc.io;

import java.util.UUID;

import com.stabilit.sc.context.ISessionContext;
import com.stabilit.sc.context.SessionContext;

public class Session implements ISession {

	private String id;
	private ISessionContext sessionContext;
	
	public Session() {
		  UUID uuid = UUID.randomUUID();
		  this.id = uuid.toString();
		  this.sessionContext = new SessionContext();
	}
	
	@Override
	public ISessionContext getContext() {
		return this.sessionContext;
	}
	
	@Override
	public String getId() {
		return this.id;
	}
}
