package com.stabilit.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Controller {
	ThreadPoolExecutor pool;
	
	public Controller(int maxThreads) {
		pool = new ThreadPoolExecutor(maxThreads, maxThreads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void run() {
		pool.execute(new QueueConsumer(RequestType.ONE));
		QueueConsumer consumer2 = new QueueConsumer(RequestType.THREE);
		QueueProducer producer1 = new QueueProducer(RequestType.ONE);
		QueueProducer producer2 = new QueueProducer(RequestType.THREE);
		new Thread(consumer2).start();
		new Thread(producer1).start();
		new Thread(producer2).start();
	}

	public void runCase2() {
		QueueConsumer consumer1 = new QueueConsumer(RequestType.ONE);
		// QueueConsumer consumer2 = new QueueConsumer(RequestType.TWO);
		QueueProducer producer1 = new QueueProducer(RequestType.ONE);
		// QueueProducer producer2 = new QueueProducer(RequestType.TWO);
		new Thread(consumer1).start();
		// new Thread(consumer2).start();
		new Thread(producer1).start();
		// new Thread(producer2).start();
	}
}