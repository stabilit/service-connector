/*
 * Main.java - main class for the Hello MBean and QueueSampler MXBean example.
 * Create the Hello MBean and QueueSampler MXBean, register them in the platform
 * MBean server, then wait forever (or until the program is interrupted).
 */

package com.example.jmx.mxbean;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MainMap {
	/*
	 * For simplicity, we declare "throws Exception". Real programs will usually
	 * want finer-grained exception handling.
	 */
	public static void main(String[] args) throws Exception {
		// Get the Platform MBean Server
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		// Construct the ObjectName for the QueueSampler MXBean we will register
		ObjectName mxbeanName = new ObjectName("com.example:type=registry");
		
		MapBean<String> mapBean = new MapBean<String>();
		mapBean.setAttribute("1","Request-1");
		mapBean.setAttribute("2","Request-2");
		
		MapBean<String> mapBean1 = new MapBean<String>();
		mapBean1.setAttribute("1","Request-1");
		mapBean1.setAttribute("2","Request-2");
		
		
		SessionRegistry.getCurrentInstance().add("mapBean", mapBean);
		SessionRegistry.getCurrentInstance().add("mapBean1", mapBean1);
		
		// Register the Queue Sampler MXBean
		mbs.registerMBean(SessionRegistry.getCurrentInstance(), mxbeanName);

		// Wait forever
		System.out.println("Waiting for incoming requests...");
		Thread.sleep(Long.MAX_VALUE);
	}
}
