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
package org.serviceconnector.scmp;

/**
 * The Interface ISCMPCallback. Interface for callback on SCMP level.
 * 
 * @author JTraber
 */
public interface ISCMPMessageCallback {

	/**
	 * Callback. This method gets informed when a message received.
	 * 
	 * @param scmpReply
	 *            the scmp reply
	 * @throws Exception
	 *             the exception
	 */
	public abstract void receive(SCMPMessage scmpReply) throws Exception;

	/**
	 * Callback. This method gets informed in case of an error.
	 * 
	 * @param ex
	 *            the ex
	 */
	public abstract void receive(Exception ex);
}
