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

import java.util.Iterator;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.org.apache.bcel.internal.generic.DMUL;

/**
 * @author JTraber
 * 
 */
public class Client {	

	public static void main(String[] args) {
		try {
			// Create an RMI connector client
			// 
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://localhost:9999/server");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			ClientListener listener = new ClientListener();
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			waitForEnterPressed();

			// Get domains from MBeanServer
			// 
			String domains[] = mbsc.getDomains();
			for (int i = 0; i < domains.length; i++) {
				System.out.println("Domain[" + i + "] = " + domains[i]);
			}
			waitForEnterPressed();

			String domain = domains[1];

			ObjectName stdMBeanName = new ObjectName("com.stabilit.jmx.dmbean:type=com.stabilit.jmx.dmbean.SimpleStandard");
			
			// Create SimpleStandard MBean //
			ObjectName mbeanName = new ObjectName(domain
					+ ":type=com.stabilit.jmx.dmbean.SimpleStandard,index=2");
			mbsc.createMBean("com.stabilit.jmx.dmbean.SimpleStandard", stdMBeanName, null, null);
			waitForEnterPressed();

			// Create SimpleDynamic MBean
			// 
			ObjectName dynMBeanName = new ObjectName(domain
					+ ":type=com.stabilit.jmx.dmbean.SimpleDynamic,index=2");
			echo("\nCreate SimpleDynamic MBean...");
			mbsc.createMBean("com.stabilit.jmx.dmbean.SimpleDynamic", dynMBeanName, null, null);
			waitForEnterPressed();

			// Get MBean count
			// 
			echo("\nMBean count = " + mbsc.getMBeanCount());

			// Query MBean names
			// 
			echo("\nQuery MBeanServer MBeans:");
			Set names = mbsc.queryNames(null, null);
			for (Iterator i = names.iterator(); i.hasNext();) {
				echo("ObjectName = " + (ObjectName) i.next());
			}
			waitForEnterPressed();

			mbsc.setAttribute(stdMBeanName, new Attribute("State",
					"changed state"));

			SimpleStandardMBean proxy = (SimpleStandardMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbsc, stdMBeanName,
							SimpleStandardMBean.class, false);
			echo("\nState = " + proxy.getState());

			ClientListener listener1 = new ClientListener();
			mbsc.addNotificationListener(stdMBeanName, listener1, null, null);

			mbsc.invoke(stdMBeanName, "reset", null, null);

			mbsc.removeNotificationListener(stdMBeanName, listener1);
			mbsc.unregisterMBean(stdMBeanName);

			jmxc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void waitForEnterPressed() {
		System.out.println("wait for enter press!");
	}

	private static void echo(String out) {
		System.out.println(out);
	}
}
