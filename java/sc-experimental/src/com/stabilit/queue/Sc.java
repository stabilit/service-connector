package com.stabilit.queue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sc {

	private static Sc instance = new Sc();
	private Map<RequestType, LinkedBlockingQueue<Request>> queues;
	private Lock queueLock = new ReentrantLock();

	private Sc() {
		queues = new ConcurrentHashMap<RequestType, LinkedBlockingQueue<Request>>();
	}

	public static Sc getInstance() {
		return instance;
	}

	public boolean put(String request, RequestType type) {
		Request request2 = new Request(request, type);

		try {
			queueLock.lock();

			LinkedBlockingQueue<Request> queue = queues.get(type);

			if (queue == null) {
				queue = new LinkedBlockingQueue<Request>(10);
				queues.put(type, queue);
			}
			try {
				queue.add(request2);
			} catch (IllegalStateException e) {
				return false;
			}
		} finally {
			queueLock.unlock();
		}
		return true;
	}

	public Request poll(RequestType type) {
		try {
			queueLock.lock();

			LinkedBlockingQueue<Request> queue = queues.get(type);

			if (queue == null)
				return null;

			Request request;
			if ((request = queue.poll()) == null) {
				return null;
			} else {
				return request;
			}
		} finally {
			queueLock.unlock();
		}
	}
}
