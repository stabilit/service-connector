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
package org.serviceconnector.net.res;

import org.serviceconnector.util.XMLDumpWriter;


/**
 * The Interface IEndpoint.
 * 
 * @author JTraber
 */
public interface IEndpoint {

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the host
	 */
	public void setHost(String host);

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the port
	 */
	public void setPort(int port);
	
	/**
	 * Destroys endpoint.
	 */
	public void destroy();

	/**
	 * Creates an endpoint.
	 */
	public void create();

	/**
	 * Starts listen asynchronous. Starts listener in a separate thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void startsListenAsync() throws Exception;

	/**
	 * Start listen synchronous. Starts listener in current thread. Does not give back control.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void startListenSync() throws Exception;

	/**
	 * Stop listening.
	 */
	public void stopListening();

	/**
	 * Gets the responder.
	 * 
	 * @return the responder
	 */
	public IResponder getResponder();

	/**
	 * Sets the responder.
	 * 
	 * @param resp
	 *            the new responder
	 */
	public void setResponder(IResponder resp);
	
	/**
	 * Dump the endpoint into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception;
}
