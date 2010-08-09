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
package com.stabilit.scm.common.scmp;

/**
 * The Interface ISCMPSynchronousCallback.
 */
public interface ISCMPSynchronousCallback extends ISCMPCallback {

	/**
	 * Callback.
	 * 
	 * @param scmpReply
	 *            the scmp reply
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public abstract void callback(SCMPMessage scmpReply) throws Exception;

	/**
	 * Callback.
	 * 
	 * @param th
	 *            the error
	 */
	@Override
	public abstract void callback(Throwable th);

	/**
	 * Gets the message synchronous. Waits until message/fault received or operation timeout occurs. Longest time to
	 * wait depends on Constants.SERVICE_LEVEL_OPERATION_TIMEOUT_MILLIS.
	 * 
	 * @return the message sync
	 * @throws Exception 
	 */
	public abstract SCMPMessage getMessageSync() throws Exception;

	/**
	 * Careful, be aware of timeout concept if you use this method. Should not be used in normal cases. Gets the message
	 * synchronous. Waits until message/fault received or time you hand over runs out.<br>
	 * <br>
	 * Hand over 0 value if you like to wait forever for some reason. Might be useful for test purposes.
	 * 
	 * @param timeoutInMillis
	 *            the timeout in milliseconds
	 * @return the message sync
	 * @throws Exception 
	 */
	public abstract SCMPMessage getMessageSync(int timeoutInMillis) throws Exception;

}
