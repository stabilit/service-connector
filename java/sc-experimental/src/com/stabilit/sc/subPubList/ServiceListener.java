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
public class ServiceListener implements Runnable {

	private SubPubList pubList;
	private String name;

	public ServiceListener(SubPubList subPubList, String serviceName) {
		this.pubList = subPubList;
		this.name = serviceName;
	}

	@Override
	public void run() {
		while (true) {
			String value = pubList.getNextMsg(name);
			if (value == null) {
				System.out.println("No Msg found");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else {
				System.out.println("Got Message, " + name + " : " + value);
			}
		}
	}
}
