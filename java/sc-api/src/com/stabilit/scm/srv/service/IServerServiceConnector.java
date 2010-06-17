/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.srv.service;



/**
 * @author JTraber
 *
 */
public interface IServerServiceConnector {
	
	/**
	 * Connects to SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void connect() throws Exception;

	/**
	 * Disconnects from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void disconnect() throws Exception;

	public void publish(String string, String mask, Object data);

	/**
	 * Sets the attribute. Attributes for ServiceConnector.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public void setAttribute(String name, Object value);

	public int getNumberOfThreads();

	public void setNumberOfThreads(int numberOfThreads);

	public String getConnectionKey();

	public void setConnectionKey(String connectionKey);

	public String getHost();
	
	public int getPort();
}
