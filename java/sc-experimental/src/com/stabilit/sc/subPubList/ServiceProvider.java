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
package com.stabilit.sc.subPubList;

/**
 * @author JTraber
 * 
 */
public class ServiceProvider implements Runnable {

	private SubPubList pubList;
	private String name;

	public ServiceProvider(SubPubList subPubList, String serviceName) {
		this.pubList = subPubList;
		this.name = serviceName;
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {

			String msg = name + "" + i;
			i++;
			pubList.putNewMsg(msg);
			System.out.println("Put Message, " + name + " : " + msg);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			continue;
		}
	}
}
