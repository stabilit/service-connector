package com.stabilit.jmx.mbean;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.monitor.MonitorNotification;
import javax.management.remote.NotificationResult;

import com.sun.jmx.remote.internal.NotificationBuffer;
import com.sun.jmx.remote.internal.NotificationBufferFilter;

public class CounterMonitorNotificationsListener implements
		NotificationListener, NotificationBuffer {

	@Override
	public void handleNotification(Notification notification, Object handback) {
		MonitorNotification monitorNotification = (MonitorNotification) (notification);

		System.out.println("*** COUNTER NOTIFICATION RECEIVED ***");
		System.out.println("Notification number: "
				+ monitorNotification.getSequenceNumber());
		System.out.println("Notification type: "
				+ monitorNotification.getType());
		System.out.println("Notification message: "
				+ monitorNotification.getMessage());
		System.out.println("Notification object: "
				+ monitorNotification.getObservedObject());
		System.out.println("Notification attribute: "
				+ monitorNotification.getObservedAttribute());
		System.out.println("Notification trigger: "
				+ monitorNotification.getTrigger());
		System.out.println("Notification gauge: "
				+ monitorNotification.getDerivedGauge());
		((Counter) handback).initCounter();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public NotificationResult fetchNotifications(
			NotificationBufferFilter filter, long startSequenceNumber,
			long timeout, int maxNotifications) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

}
