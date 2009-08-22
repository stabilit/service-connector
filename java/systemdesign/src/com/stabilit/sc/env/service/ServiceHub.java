package com.stabilit.sc.env.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceHub {

	private Map<String, IQueue> queueMap;
	
	public ServiceHub() {
		queueMap = new HashMap<String, IQueue>();
	}
	
	public IQueue createQueue(String key) {
		IQueue queue = new ServiceHubQueue();
		queueMap.put(key, queue);
		return queue;
	}
	
	public void destroyQueue(String key) {
		IQueue queue = this.queueMap.get(key);
		if (queue != null) {
			queue.destroy();
		}
	}
	
	public void destroyQueue(IQueue queue) {
		queue.destroy();
		queueMap.remove(queue);
	}
}
