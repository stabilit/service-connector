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
package com.stabilit.jmx.jdmkmlet;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * @author JTraber
 * 
 */
public class MLetTest {

	public static void main(String[] args) throws Exception {
		// Instantiate the MBean server
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		String domain = server.getDefaultDomain();
		// Create a new MLet MBean and add it to the MBeanServer.
		String mletClass = "javax.management.loading.MLet";
		ObjectName mletName = new ObjectName(domain + ":name=" + mletClass);
		server.createMBean(mletClass, mletName);

		URL url_1 = MLetTest.class.getResource("mlets.html");
		Object mletParams_1[] = { url_1.toString() };
		String mletSignature_1[] = { "java.lang.String" };
//		server.invoke(mletName, "addURL", mletParams_1, mletSignature_1);
		
		printsMBeansFromURL(mletName, mletParams_1, mletSignature_1, server);
		
		// Create a Square MBean from its class in the Square.jar file.
//		String squareClass = "com.stabilit.jmx.jdmkmlet.Square";
//		ObjectName squareName = new ObjectName("MLetExample:name="
//				+ squareClass);
//		Object squareParams[] = { new Integer(10) };
//		String squareSignature[] = { "java.lang.Integer" };
//		server.createMBean(squareClass, squareName, mletName, squareParams,
//				squareSignature);
	}

	private static void printsMBeansFromURL(final ObjectName mletName,
			final Object mletParams_1[], final String mletSignature_1[], final MBeanServer server)
			throws Exception {

		Set<?> mbeanSet = (Set<?>) server.invoke(mletName, "getMBeansFromURL",
				mletParams_1, mletSignature_1);

		for (Iterator<?> i = mbeanSet.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof ObjectInstance) {
				// Success, we display the new MBean's name
				System.out.println("\tOBJECT NAME = "
						+ ((ObjectInstance) element).getObjectName());
			} else {
				// Failure, we display why
				System.out.println("\tEXCEPTION = "
						+ ((Throwable) element).getMessage());
			}
			Object squareParams[] = { new Integer(10) };
			String squareSignature[] = { "java.lang.Integer" };
			server.createMBean("com.stabilit.jmx.jdmkmlet.Square", ((ObjectInstance) element).getObjectName(), mletName, squareParams, squareSignature);
			int ig = 5;
		
		}
		
		
	}
}
