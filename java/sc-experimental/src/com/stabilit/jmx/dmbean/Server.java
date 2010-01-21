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

import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author JTraber
 * 
 */
public class Server {
	public static void main(String[] args) {
		try {

			MBeanServer mbs = MBeanServerFactory.createMBeanServer();
			waitForEnterPressed();

			String domain = mbs.getDefaultDomain();
			waitForEnterPressed();
			domain = "com.stabilit.jmx.dmbean";
			String mbeanClassName = "com.stabilit.jmx.dmbean.SimpleStandard";
			String mbeanObjectNameStr = domain + ":type=" + mbeanClassName
					+ ",index=1";
			ObjectName mbeanObjectName = createSimpleMBean(mbs, mbeanClassName,
					mbeanObjectNameStr);
			waitForEnterPressed();

			printMBeanInfo(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			manageSimpleMBean(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			mbeanClassName = "com.stabilit.jmx.dmbean.SimpleDynamic";
			mbeanObjectNameStr = domain + ":type=" + mbeanClassName
					+ ",index=1";
			mbeanObjectName = createSimpleMBean(mbs, mbeanClassName,
					mbeanObjectNameStr);
			waitForEnterPressed();

			printMBeanInfo(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			manageSimpleMBean(mbs, mbeanObjectName, mbeanClassName);
			waitForEnterPressed();

			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://localhost:9999/server");
			Map<String, String> map = new HashMap<String, String>();
			map.put("server", "server");
			JMXConnectorServer cs = JMXConnectorServerFactory
					.newJMXConnectorServer(url, map, mbs);
			cs.start();
			waitForEnterPressed();
			cs.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ObjectName createSimpleMBean(MBeanServer mbs,
			String mbeanClassName, String mbeanObjectNameStr) {
		echo("\n>>> Create the " + mbeanClassName
				+ " MBean within the MBeanServer");
		echo("ObjectName = " + mbeanObjectNameStr);
		try {
			ObjectName mbeanObjectName = ObjectName
					.getInstance(mbeanObjectNameStr);
			mbs.createMBean(mbeanClassName, mbeanObjectName);
			return mbeanObjectName;
		} catch (Exception e) {
			echo("!!! Could not create the " + mbeanClassName + " MBean !!!");
			e.printStackTrace();
			echo("\nEXITING...\n");
			System.exit(1);
		}
		return null;
	}

	private static void printMBeanInfo(MBeanServer mbs,
			ObjectName mbeanObjectName, String mbeanClassName) {
		MBeanInfo info = null;
		try {
			info = mbs.getMBeanInfo(mbeanObjectName);
		} catch (Exception e) {
			echo("!!! Could not get MBeanInfo object for " + mbeanClassName
					+ " !!!");
			e.printStackTrace();
			return;
		}

		MBeanAttributeInfo[] attrInfo = info.getAttributes();
		if (attrInfo.length > 0) {
			for (int i = 0; i < attrInfo.length; i++) {
				echo(" ** NAME: 	" + attrInfo[i].getName());
				echo("    DESCR: 	" + attrInfo[i].getDescription());
				echo("    TYPE: 	" + attrInfo[i].getType() + "READ: "
						+ attrInfo[i].isReadable() + "WRITE: "
						+ attrInfo[i].isWritable());
			}
		} else
			echo(" ** No attributes **");
	}

	private static void manageSimpleMBean(MBeanServer mbs,
			ObjectName mbeanObjectName, String mbeanClassName) {
		try {
			printSimpleAttributes(mbs, mbeanObjectName);

			Attribute stateAttribute = new Attribute("State", "new state");
			mbs.setAttribute(mbeanObjectName, stateAttribute);

			printSimpleAttributes(mbs, mbeanObjectName);

			echo("\n    Invoking reset operation...");
			mbs.invoke(mbeanObjectName, "reset", null, null);

			printSimpleAttributes(mbs, mbeanObjectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printSimpleAttributes(MBeanServer mbs,
			ObjectName mbeanObjectName) {
		try {
			String State = (String) mbs.getAttribute(mbeanObjectName, "State");
			Integer NbChanges = (Integer) mbs.getAttribute(mbeanObjectName,
					"NbChanges");
		} catch (Exception e) {
			echo("!!! Could not read attributes !!!");
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
