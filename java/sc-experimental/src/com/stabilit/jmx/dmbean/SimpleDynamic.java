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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

/**
 * @author JTraber
 * 
 */
public class SimpleDynamic extends NotificationBroadcasterSupport implements
		DynamicMBean {

	private final Properties properties;
	private final String propertyFileName;

	public SimpleDynamic() throws IOException {
		this.propertyFileName = "C:\\stabilit\\projects\\EUREX\\SC\\dev\\eclipse_workspace\\envUtil\\src\\com\\stabilit\\jmx\\dmbean\\props.properties";
		properties = new Properties();
		load();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	@Override
	public AttributeList getAttributes(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		SortedSet<String> names = new TreeSet<String>();
		for (Object name : properties.keySet())
			names.add((String) name);
		MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size()];
		Iterator<String> it = names.iterator();
		for (int i = 0; i < attrs.length; i++) {
			String name = it.next();
			attrs[i] = new MBeanAttributeInfo(name, "java.lang.String",
					"Property " + name, true, // isReadable
					true, // isWritable
					false); // isIs
		}
		MBeanOperationInfo[] opers = { new MBeanOperationInfo("reload",
				"Reload properties from file", null, // no parameters
				"void", MBeanOperationInfo.ACTION) };
		return new MBeanInfo(this.getClass().getName(),
				"Property Manager MBean", attrs, null, // constructors
				opers, null); // notifications

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 */
	@Override
	public Object invoke(String arg0, Object[] arg1, String[] arg2)
			throws MBeanException, ReflectionException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.DynamicMBean#setAttributes(javax.management.AttributeList
	 * )
	 */
	@Override
	public AttributeList setAttributes(AttributeList arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void load() throws IOException {
		InputStream input = new FileInputStream(propertyFileName);
		properties.load(input);
		input.close();
	}

}
