package com.stabilit.sc.context;

public interface IApplicationContext extends IContext {

	public String[] getArgs();
	
	public void setArgs(String[] args) throws Exception;
}
