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
 * The Class CommunicatorConfig.
 * 
 * @author JTraber
 */
public class CommunicatorConfig implements ICommunicatorConfig {

	/** The communicator name. */
	private String communicatorName;
	/** The port. */
	private int port;
	/** The host. */
	private String host;
	/** The connectionKey. */
	private String connectionKey;
	/** The number of threads. */
	private int numberOfThreads;
	/** The max pool size. */
	public int maxPoolSize;

	/**
	 * Instantiates a new communicator configuration.
	 */
	CommunicatorConfig() {
	}

	/**
	 * Instantiates a new communicator configuration.
	 * 
	 * @param communicatorName
	 *            the communicator name
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionKey
	 *            the connectionKey
	 * @param numberOfThreads
	 *            the number of threads
	 * @param maxPoolSize
	 *            the max pool size
	 */
	public CommunicatorConfig(String communicatorName, String host, int port, String connectionKey,
			int numberOfThreads, int maxPoolSize) {
		this.communicatorName = communicatorName;
		this.maxPoolSize = maxPoolSize;
		this.port = port;
		this.host = host;
		this.connectionKey = connectionKey;
		this.numberOfThreads = numberOfThreads;
	}

	/**
	 * The Constructor.
	 * 
	 * @param respName
	 *            the responder name
	 */
	public CommunicatorConfig(String respName) {
		this.communicatorName = respName;
	}

	/** {@inheritDoc} */
	@Override
	public String getCommunicatorName() {
		return communicatorName;
	}

	/**
	 * Sets the communicator name.
	 * 
	 * @param respName
	 *            the new communicator name
	 */
	public void setCommunicatorName(String respName) {
		this.communicatorName = respName;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the new host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	/**
	 * Sets the number of threads.
	 * 
	 * @param numberOfThreads
	 *            the new number of threads
	 */
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionKey() {
		return this.connectionKey;
	}

	/**
	 * Sets the connection key.
	 * 
	 * @param connectionKey
	 *            the new connection key
	 */
	public void setConnectionKey(String connectionKey) {
		this.connectionKey = connectionKey;
	}

	/**
	 * Gets the max pool size.
	 * 
	 * @return the max pool size
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * Sets the max pool size.
	 * 
	 * @param maxPoolSize
	 *            the new max pool size
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
}
