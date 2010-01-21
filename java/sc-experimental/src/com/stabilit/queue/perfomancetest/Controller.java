package com.stabilit.queue.perfomancetest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Controller {
	private ThreadPoolExecutor pool;
	private int numberOfCons;
	private int numberOfProd;
	private Sc2 sc2 = Sc2.getInstance();

	public Controller(int numberOfCons, int numberOfProd, int maxThreads) {
		pool = new ThreadPoolExecutor(maxThreads, maxThreads, 10,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		this.numberOfCons = numberOfCons;
		this.numberOfProd = numberOfProd;
	}

	public void runMultipleThreads() {

		long startTime = System.currentTimeMillis();

		for (int count = 0; count < numberOfCons; count++) {
			pool
					.execute(new QueueConsumer("Consumer" + count, 250000,
							startTime));
		}
		
		for (int count = 0; count < numberOfProd; count++) {
			pool
					.execute(new QueueProducer("Producer" + count, 3125000,
							startTime));
		}
	}

	public void runSingleThread() {
		long startProdTime = System.currentTimeMillis();
		System.out.println("Start producing Time : " + startProdTime);
		Thread prod = new Thread(new QueueProducer("Producer1", 100000,
				startProdTime));
		prod.start();

		try {
			prod.join();
			long endProdTime = System.currentTimeMillis();
			System.out.println("End/Start producing/consuming Time : "
					+ endProdTime);
			Thread cons = new Thread(new QueueConsumer("Consumer1", 100000,
					startProdTime));
			cons.start();
			cons.join();
			long endConsTime = System.currentTimeMillis();
			System.out.println("End consuming Time : " + endConsTime);
			System.out.println(" ---- Total Time : "
					+ (endConsTime - startProdTime) + " Ms"
					+ " ---- Total Producing Time : "
					+ (endProdTime - startProdTime) + " Ms"
					+ " ---- Total Consuming Time : "
					+ (endConsTime - endProdTime) + " Ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}