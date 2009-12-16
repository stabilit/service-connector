package com.mina.http.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTester {

	private ThreadPoolExecutor pool;
	private int numberOfClients;
	private int maxThreads;
	private int numberOfConn;
	Logger logger = LoggerFactory.getLogger(PerformanceTester.class);

	public PerformanceTester(int numberOfClients, int maxThreads,
			int numberOfConn) {
		this.numberOfClients = numberOfClients;
		this.maxThreads = maxThreads;
		this.numberOfConn = numberOfConn;
	}

	public static void main(String[] args) {
		PerformanceTester tester = new PerformanceTester(10, 10, 20);
		tester.doIt();
	}

	public void doIt() {
		pool = new ThreadPoolExecutor(2, maxThreads, 10,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {
			pool.execute(new HttpClient(numberOfConn));
		}

		while (pool.getActiveCount() != 0)
			;
		System.out.println("is terminated before: " + pool.isTerminated());
		System.out.println("is shutdown before: " + pool.isShutdown());
		pool.shutdown();
		System.out.println("is terminated after: " + pool.isTerminated());
		System.out.println("is shutdown after: " + pool.isShutdown());
		pool.shutdownNow();
		System.out.println("is terminated after2: " + pool.isTerminated());
		System.out.println("is shutdown after2: " + pool.isShutdown());
		System.out.println("Job Done in: "
				+ (System.currentTimeMillis() - startTime) + " Ms");
	}
}
