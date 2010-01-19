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
package com.stabilit.sc.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.netty.http.server.NettyHttpServer;

/**
 * @author JTraber
 * 
 */
public class Sc {

	public static void main(String[] args) throws Exception {
		Sc sc = new Sc();
		sc.start();
	}

	public void start() throws Exception {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(16, 16, 1000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//		pool.execute(new ShowThreadsInMain());
		
//		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//		ObjectName mxbeanName = new ObjectName(
//				"com.stabilit.sc.queue:type=RequestQueuer");
//		mbs.registerMBean(RequestQueuer.getInstance(), mxbeanName);		
		
		pool.execute(new NettyHttpServer());
		pool.execute(new RequestQueueManager());
	}
}
