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
package com.stabilit.scm.common.conf;

/**
 * The Interface ICommunicatorConfig.
 * 
 * @author JTraber
 */
public interface ICommunicatorConfig {

	/**
	 * Gets the communicator name.
	 * 
	 * @return the communicator name
	 */
	public abstract String getCommunicatorName();

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public abstract int getPort();

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Gets the number of threads.
	 * 
	 * @return the number of threads
	 */
	public int getNumberOfThreads();

	public String getConnectionType();

	/**
	 * Gets the max pool size.
	 * 
	 * @return the max pool size
	 */
	public int getMaxPoolSize();
	
	/**
	 * Gets the keep alive interval.
	 *
	 * @return the keep alive interval
	 */
	public int getKeepAliveInterval();
}