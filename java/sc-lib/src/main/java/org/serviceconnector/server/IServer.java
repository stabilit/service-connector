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
package org.serviceconnector.server;

import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Interface IServer.
 */
public interface IServer {

	/**
	 * Gets the server type.
	 * 
	 * @return the type
	 */
	public abstract ServerType getType();

	/**
	 * Abort session on server.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason
	 */
	public abstract void abortSession(AbstractSession session, String reason);
	
	
	/**
	 * Dump the server into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public abstract void dump(XMLDumpWriter writer) throws Exception;
}
