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
package com.stabilit.jmx.modelermbean;

import java.io.InputStream;
import java.lang.management.ManagementFactory;

import org.apache.commons.modeler.Registry;

/**
 * The purpose of this class is to demonstrate use of ModelMBeans with Apache
 * Commons Modeler.
 */
public class CommonsModelMBeanDemonstrator {

	public void applyCommonsModeler() throws Exception {

		final String modelerMetadataFile = "simple-calculator-modeler.xml";
		final SimpleCalculator calculator = new SimpleCalculator();
		Registry registry = null;
		final InputStream modelerXmlInputStream = CommonsModelMBeanDemonstrator.class
				.getResourceAsStream(modelerMetadataFile);

		registry = Registry.getRegistry(null, null);

		registry.setMBeanServer(ManagementFactory.getPlatformMBeanServer());
		registry.loadMetadata(modelerXmlInputStream);
		registry.registerComponent(calculator,
				"modelmbean:type=commons-modeler-calculator",
				"com.stabilit.jmx.modelmbean.SimpleCalculator");
		
		RegistryController registryController = new RegistryController(registry, modelerMetadataFile);
		registry.registerComponent(registryController,
				"modelmbean:type=commons-modeler-registrycontroller",
				"com.stabilit.jmx.modelmbean.RegistryController");
	}

	public static void main(final String[] arguments) throws Exception {
		CommonsModelMBeanDemonstrator me = new CommonsModelMBeanDemonstrator();
		me.applyCommonsModeler();
		Thread.sleep(1000000);
	}
}
