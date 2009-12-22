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
package com.stabilit.jmx.springmbean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The purpose of this class is to demonstrate use of ModelMBeans with Spring.
 */
public class SpringModelMBeanDemonstrator {

	public static void main(final String[] arguments) throws InterruptedException {

		System.out.println("Spring Interface Approach");
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"/com/stabilit/jmx/springmbean/spring-mbean-simple-context.xml");

		final SimpleCalculatorIf calculator = (SimpleCalculatorIf) context
				.getBean("exposedModelMBean");
		Thread.sleep(10000000);
	}
}
