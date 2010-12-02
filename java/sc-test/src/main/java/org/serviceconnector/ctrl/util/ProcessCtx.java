/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.ctrl.util;

import org.apache.log4j.Logger;

public class ProcessCtx {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ProcessCtx.class);
	
	private Process process = null;
	private String runableFull = null;
	private String propertyFileName = null;
	private String propertyFileNameFull = null;
	private String log4jFileName = null;
	private String log4jFileNameFull = null;
	private String pidFileNameFull = null;
	private boolean running = false;
	private String serviceNames;
	
	public ProcessCtx() {
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		if(process != null) {
			this.running = true;
		}
		this.process = process;
	}

	public String getRunableFull() {
		return runableFull;
	}

	public void setRunableFull(String runableFull) {
		this.runableFull = runableFull;
	}

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

	public String getPropertyFileNameFull() {
		return propertyFileNameFull;
	}

	public void setPropertyFileNameFull(String propertyFileNameFull) {
		this.propertyFileNameFull = propertyFileNameFull;
	}

	public String getLog4jFileName() {
		return log4jFileName;
	}

	public void setLog4jFileName(String log4jFileName) {
		this.log4jFileName = log4jFileName;
	}

	public String getLog4jFileNameFull() {
		return log4jFileNameFull;
	}

	public void setLog4jFileNameFull(String log4jFileNameFull) {
		this.log4jFileNameFull = log4jFileNameFull;
	}

	public String getPidFileNameFull() {
		return pidFileNameFull;
	}

	public void setPidFileNameFull(String pidFileNameFull) {
		this.pidFileNameFull = pidFileNameFull;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setServiceNames(String serviceNames) {
		this.serviceNames = serviceNames;
	}
	
	public String getServiceNames() {
		return serviceNames;
	}
}
