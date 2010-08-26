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
package com.stabilit.scm.cln.service;

import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.common.service.ISCMessageCallback;

/**
 * The Interface ISessionService. Basic interface for session services.
 */
public interface ISessionService extends IService {

	/**
	 * Creates the session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param echoIntervalInSeconds
	 *            the echo interval, time interval a echo will be executed by the client to prevent session timeout.
	 *            Very important for SC to detect broken sessions.
	 * @param timeoutInSeconds
	 *            the echo timeout, time an SC has to observe for receiving echo reply from server. Echo gets executed
	 *            to prevent session timeout.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void createSession(String sessionInfo, int echoIntervalInSeconds, int timeoutInSeconds)
			throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request message
	 * @return the ISCMessage
	 * @throws Exception
	 *             the exception
	 */
	public abstract ISCMessage execute(ISCMessage requestMsg) throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request ISCMessage
	 * @param callback
	 *            the callback
	 * @throws Exception
	 *             the exception
	 */
	public abstract void execute(ISCMessage requestMsg, ISCMessageCallback callback) throws Exception;

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deleteSession() throws Exception;

	/** {@inheritDoc} */
	@Override
	public abstract IServiceContext getContext();
}
