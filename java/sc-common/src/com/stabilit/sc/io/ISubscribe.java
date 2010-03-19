package com.stabilit.sc.io;


public interface ISubscribe {

	public String subscribe() throws Exception;
	
	public void unsubscribe(String subscribeId);
	
	public void stopSubscriptionActionOnConnection();
	
	public boolean continueSubscriptionOnConnection();
	
	void releaseConnection();	
}
