package com.stabilit.sc.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestQueuer implements RequestQueuerMXBean {

	private static RequestQueuer instance = new RequestQueuer();
	private static BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();

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

	public void run() {

		Request request;

		while (true) {
			request = null;
			try {
				request = requestQueue.take();
			} catch (InterruptedException e) {
				continue;
			}

			System.out.println("Request wird verarbeitet: " + request);
			System.out.println("In Queue: " + getSize());
			if (request != null)
				request.getHandler().continueWork();
		}
	}
	

	@Override
	public int getSize() {
		return requestQueue.size();
	}
}
