package com.stabilit.sc.env.service;

public interface IQueue {

	public void init();
	
	public void addServiceProvider(IServiceProvider serviceProvider);

	public void removeServiceProvider(IServiceProvider serviceProvider);

	public void addServiceListener(IServiceListener serviceListener);

	public void removeServiceListener(IServiceListener serviceListener);
	
	public void destroy();
}

