package com.stabilit.sc.app.client;


public interface ISubscribe {

	public String subscribe() throws Exception;
	
	public void unsubscribe(String subscribeId) throws Exception;
	
}
