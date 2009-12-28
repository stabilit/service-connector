package com.stabilit.sc.queue;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueuer implements RequestQueuerMXBean {

	private static RequestQueuer instance = new RequestQueuer();
	private Queue<Request> requestQueue = new LinkedBlockingQueue<Request>();

	private RequestQueuer() {
	}
	
	public static RequestQueuer getInstance() {
		return instance;
	}
	
	public Request[] getRequests() {
		Request[] requests = new Request[0];
		return requestQueue.toArray(requests);
	}

	public void add(Request request) {
		requestQueue.add(request);
	}

	@Override
	public Request getRequest() {
		return requestQueue.element();
	}
}
