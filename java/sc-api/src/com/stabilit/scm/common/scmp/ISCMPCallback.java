package com.stabilit.scm.common.scmp;


public interface ISCMPCallback {

	public abstract void callback(SCMPMessage response) throws Exception;
	
	public abstract void callback(Throwable th);
	
}
