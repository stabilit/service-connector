package com.stabilit.sc.app.client;

import com.stabilit.sc.msg.ICallback;

public interface ISubscribe {

	public String subscribe(ICallback callback) throws Exception;
	
	public void unsubscribe(String subscribeId) throws Exception;
	
}
