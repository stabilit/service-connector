package com.stabilit.perfomancetest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Sc2 {

	private static Sc2 instance = new Sc2();
	private LinkedBlockingQueue<Request> queue;

	private Sc2() {
		queue = new LinkedBlockingQueue<Request>(100000);
	}

	public static Sc2 getInstance() {
		return instance;
	}

	public boolean put(String request) {
		Request request2 = new Request(request);
		try {
			queue.offer(request2, 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Request poll() throws InterruptedException {
		Request req = queue.poll(1000, TimeUnit.MILLISECONDS);
		return req;
	}
}
