package com.stabilit.sc.msg;

import com.stabilit.sc.io.SCMP;

public interface ICallback {

	public void callback(SCMP scmp) throws Exception;

	public String getSubscribeId();
	
	public void setSubscribeId(String subscribeId);

	public void sendAsyncRequest() throws Exception;
	
	public void release();
	
	public boolean isReleased();

}
