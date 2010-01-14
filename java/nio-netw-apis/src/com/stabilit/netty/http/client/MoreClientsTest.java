/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.netty.http.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JTraber
 * 
 */
public class MoreClientsTest {

	private static int numberOfClients = 15;
	private static int numberOfMsg = 1000;
	private static int countFinish = 0;
	private static long startTime;

	public static void main(String[] args) {
		
		if(args.length != 0) {
			numberOfClients = Integer.valueOf(args[0]);
			numberOfMsg = Integer.valueOf(args[1]);
		}
		
		ThreadPoolExecutor pool = new ThreadPoolExecutor(16, 16, 1000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfClients; i++) {
			NettyHttpClient client = new NettyHttpClient(numberOfMsg);
			pool.execute(client);
		}
	}

	public synchronized static void finishThread() {
		countFinish++;
		if (countFinish == numberOfClients) {
			long neededTime = System.currentTimeMillis() - startTime;
			System.out.println("Job Done in: " + neededTime + " Ms");
			double neededSeconds = neededTime / 1000D;
			System.out.println((NettyHttpClient.numberOfMsg  * numberOfClients / neededSeconds)
					+ " Messages in 1 second!");
			System.out.println("Anzahl clients : " + numberOfClients);
		}
	}
}
