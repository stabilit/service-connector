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
package com.stabilit.monitor.threads;

/**
 * @author JTraber
 * 
 */
public class ShowThreadsInMain extends Thread{

	public ShowThreadsInMain() {
		super.setName("test");
	}
	
	public static void showThreads() {
		ThreadGroup top = Thread.currentThread().getThreadGroup();

		while (top.getParent() != null)
			top = top.getParent();

		System.out.println(top);
		System.out.println();

		Thread threadArray[] = new Thread[top.activeCount()];

		top.enumerate(threadArray);

		for (int i = 0; i < threadArray.length; i++) {
			System.out.println(threadArray[i]);
		}
		System.out.println("--------------------------------------------------");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			ShowThreadsInMain.showThreads();
		}
	}
}
