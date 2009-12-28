package com.stabilit.sc.app.client.mina.http;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTester {

	private ThreadPoolExecutor pool;
	private int numberOfClients;
	private int numberOfConn;
	private int messageSize;
	Logger logger = LoggerFactory.getLogger(PerformanceTester.class);

	public PerformanceTester(int numberOfClients,int numberOfConn, int messageSize) {
		this.numberOfClients = numberOfClients;
		this.numberOfConn = numberOfConn;
		this.messageSize = messageSize;
	}

	public static void main(String[] args) {
		int numberOfClients = 80;
		int numberOfConn = 1;
		int messageSize = 128;

		PerformanceTester tester = new PerformanceTester(numberOfClients,
				numberOfConn, messageSize);
		tester.doIt();
	}

	public void doIt() {
		pool = new ThreadPoolExecutor(numberOfClients, numberOfClients, 10,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {
			pool.execute(new HttpClient(numberOfConn, messageSize));
		}

		while (pool.getActiveCount() != 0)
			;
		pool.shutdown();
		long neededTime = System.currentTimeMillis() - startTime;
		System.out.println("Job Done in: "
				+ (System.currentTimeMillis() - startTime) + " Ms");
		double neededSeconds = neededTime / 1000;
		System.out.println((numberOfConn*numberOfClients / neededSeconds) + " Connections in 1 second!");
		System.out.println("Anzahl clients: " + numberOfClients);
		System.out.println("Anzahl connections pro client: " + numberOfConn);
		System.out.println("Nachrichten Grösse: " + messageSize);
	}
}
