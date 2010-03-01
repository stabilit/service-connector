package com.stabilit.sc.app.client;

import com.stabilit.sc.msg.ISCListener;

public interface ISubscribe {

	public String subscribe() throws Exception;
	
	public void unsubscribe(String subscribeId) throws Exception;
	
}
