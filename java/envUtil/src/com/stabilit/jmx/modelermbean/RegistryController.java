package com.stabilit.jmx.modelermbean;

import java.util.Calendar;

import org.apache.commons.modeler.Registry;

public class RegistryController {
	private Registry registry = null;
	private String modelerMetadataFile = null;

	public RegistryController(Registry registry, String modelerMetadataFile) {
		super();
		this.registry = registry;
		this.modelerMetadataFile = modelerMetadataFile;
	}

	public void laodRegistryMetaDateNew() throws Exception {
		registry.loadMetadata(RegistryController.class
				.getResourceAsStream(modelerMetadataFile));
		registry.unregisterComponent("modelmbean:type=commons-modeler-calculator");
		registry.registerComponent(new SimpleCalculator(),
				"modelmbean:type=commons-modeler-calculator",
				"com.stabilit.jmx.modelmbean.SimpleCalculator");
	}
}
