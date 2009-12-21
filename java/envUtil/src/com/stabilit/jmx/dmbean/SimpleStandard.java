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
package com.stabilit.jmx.dmbean;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

/**
 * @author JTraber
 * 
 */
public class SimpleStandard extends NotificationBroadcasterSupport implements
		SimpleStandardMBean {
	public String getState() {
		return state;
	}

	public void setState(String s) {
		state = s;
		nbChanges++;
	}

	public int getNbChanges() {
		return nbChanges;
	}

	public void reset() {
		AttributeChangeNotification acn = new AttributeChangeNotification(this,
				0, 0, "NbChanges reset", "NbChanges", "Integer", new Integer(
						nbChanges), new Integer(0));
		state = "initial state";
		nbChanges = 0;
		nbResets++;
		sendNotification(acn);
	}

	public int getNbResets() {
		return nbResets;
	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return new MBeanNotificationInfo[] { new MBeanNotificationInfo(
				new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
				AttributeChangeNotification.class.getName(),
				"This notification is emitted when the reset() method is called.") };
	}

	private String state = "initial state";
	private int nbChanges = 0;
	private int nbResets = 0;

}
