package com.stabilit.sc.app.server;


public interface IServer {

	public void create() throws Exception;

	public void run() throws Exception;
	
	public void destroy() throws Exception;

}
