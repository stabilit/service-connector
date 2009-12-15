package com.mina.http.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.common.IoFilter;
import org.apache.mina.filter.LoggingFilter;

public class PerformanceTester {

	private ThreadPoolExecutor pool;
	private int numberOfClients;
	private int maxThreads;

	public PerformanceTester(int numberOfClients, int maxThreads) {
		this.numberOfClients = numberOfClients;
		this.maxThreads = maxThreads;
	}

	public static void main(String[] args) {
		PerformanceTester tester = new PerformanceTester(1, 10);
		tester.doIt();
	}

	public void doIt() {
		pool = new ThreadPoolExecutor(maxThreads, maxThreads, 10,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {
			pool.execute(new HttpClient(100));
		}

		while (pool.getActiveCount() != 0);
		System.out.println("Job Done in: "
				+ (System.currentTimeMillis() - startTime) + " Ms");
	}
}
