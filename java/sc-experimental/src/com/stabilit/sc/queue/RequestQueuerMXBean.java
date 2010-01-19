package com.stabilit.sc.queue;


public interface RequestQueuerMXBean {
	RequestMXBean[] getRequests();
	int getSize();
}
