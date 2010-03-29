package com.stabilit.sc.io;

import java.util.UUID;

import com.stabilit.sc.util.MapBean;

public class Session extends MapBean<Object> implements ISession {

	private String id;
	
	public Session() {
		  UUID uuid = UUID.randomUUID();
		  this.id = uuid.toString();
	}	
	
	@Override
	public String getId() {
		return this.id;
	}
}
