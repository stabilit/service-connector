package com.stabilit.mbean;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.monitor.CounterMonitor;


public class Starter {

	public static void main(String[] args) throws Exception {
		CounterMonitor cm = new CounterMonitor();
		Counter count = new Counter();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		cm.addObservedObject(new ObjectName("com.stabilit.mbean:type=Counter"));
		cm.setObservedAttribute("Count");
		cm.setGranularityPeriod(500);
		cm.setDifferenceMode(false);
		cm.setInitThreshold(new Integer(5));
		cm.setNotify(true);
		mbs.registerMBean(cm, new ObjectName(
				"Services:type=CounterMonitor,name=CounterMonitor_0"));
		mbs.addNotificationListener(new ObjectName(
				"Services:type=CounterMonitor,name=CounterMonitor_0"),
				new CounterMonitorNotificationsListener(), null, count);
		cm.start();		
		mbs.registerMBean(count, new ObjectName("com.stabilit.mbean:type=Counter"));
		//mbs.registerMBean(count.getCounters(), new ObjectName("com.stabilit.mbean:type=Counter"));
		count.count();
	}
}
