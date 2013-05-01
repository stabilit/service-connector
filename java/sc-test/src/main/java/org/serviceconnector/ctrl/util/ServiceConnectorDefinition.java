package org.serviceconnector.ctrl.util;

public class ServiceConnectorDefinition {

	private String scName;
	private String properyFileName;
	private String log4jFileName;

	public ServiceConnectorDefinition(String scName, String properyFileName, String log4jFileName) {
		this.scName = scName;
		this.properyFileName = properyFileName;
		this.log4jFileName = log4jFileName;
	}

	public String getLog4jFileName() {
		return log4jFileName;
	}

	public String getProperyFileName() {
		return properyFileName;
	}
	
	public String getName() {
		return this.scName;
	}
}
