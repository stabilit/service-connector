/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.conf;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.ICommunicatorConfig;


/**
 * The Class RequesterConfigPool. RequesterConfigPool may hold more than one configuration for a requester, is
 * represented by <code>RequesterConfig</code>.
 * 
 * @author JTraber
 */
public class RequesterConfigPool extends CommunicatorConfigPool {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RequesterConfigPool.class);
	
	/**
	 * Loads configuration from a file.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void load(String fileName) throws Exception {
		this.loadRequesterConfig(fileName);
	}

	/**
	 * Gets the requester configuration list.
	 * 
	 * @return the requester configuration list
	 */
	public List<ICommunicatorConfig> getRequesterConfigList() {
		return this.getCommunicatorConfigList();
	}

	/**
	 * Gets the requester configuration. Returns the requester configuration of first requester in configuration file.
	 * Usually only one requester is configured.
	 * 
	 * @return the requester configuration
	 */
	public ICommunicatorConfig getRequesterConfig() {
		return this.getCommunicatorConfigList().get(0);
	}
}
