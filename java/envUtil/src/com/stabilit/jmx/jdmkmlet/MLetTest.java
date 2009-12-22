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

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
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
		
		Object mletParams_1[] = {"file:\\\\\\c:\\stabilit\\projects\\EUREX\\SC\\dev\\eclipse_workspace\\envUtil\\src\\com\\stabilit\\jmx\\jdmkmlet\\mlet.html"};
		String mletSignature_1[] = {"java.lang.String"};
		server.invoke(mletName, "addURL", mletParams_1, mletSignature_1);
		
		// Create a Square MBean from its class in the Square.jar file.
		String squareClass = "Square";
		ObjectName squareName = new ObjectName(
		"MLetExample:name=" + squareClass);
		Object squareParams[] = {new Integer(10)};
		String squareSignature[] = {"java.lang.Integer"};
		server.createMBean(squareClass, squareName, mletName,
		squareParams, squareSignature);
	}
}
