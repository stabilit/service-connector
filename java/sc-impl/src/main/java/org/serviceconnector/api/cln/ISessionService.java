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
package org.serviceconnector.api.cln;

import org.serviceconnector.api.SCMessage;
import org.serviceconnector.service.ISCMessageCallback;

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
	 *            the echo timeout, time an SC has to observe for receiving echo reply from server.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void createSession(String sessionInfo, int echoIntervalInSeconds, int timeoutInSeconds)
			throws Exception;

	/**
	 * Creates the session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param echoIntervalInSeconds
	 *            the echo interval, time interval a echo will be executed by the client to prevent session timeout.
	 *            Very important for SC to detect broken sessions.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void createSession(String sessionInfo, int echoIntervalInSeconds) throws Exception;

	/**
	 * Creates the session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param echoIntervalInSeconds
	 *            the echo interval, time interval a echo will be executed by the client to prevent session timeout.
	 *            Very important for SC to detect broken sessions.
	 * @param timeoutInSeconds
	 *            the echo timeout, time an SC has to observe for receiving echo reply from server.
	 * @param data
	 *            the data
	 * @throws Exception
	 *             the exception
	 */
	public abstract void createSession(String sessionInfo, int echoIntervalInSeconds, int timeoutInSeconds, Object data)
			throws Exception;

	/**
	 * Creates the session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param echoIntervalInSeconds
	 *            the echo interval, time interval a echo will be executed by the client to prevent session timeout.
	 *            Very important for SC to detect broken sessions.
	 * @param data
	 *            the data
	 * @throws Exception
	 *             the exception
	 */
	public abstract void createSession(String sessionInfo, int echoIntervalInSeconds, Object data) throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request message
	 * @return the ISCMessage
	 * @throws Exception
	 *             the exception
	 */
	public abstract SCMessage execute(SCMessage requestMsg) throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request message
	 * @param timeoutInSeconds
	 *            the timeout in seconds
	 * @return the SCMessage
	 * @throws Exception
	 *             the exception
	 */
	public abstract SCMessage execute(SCMessage requestMsg, int timeoutInSeconds) throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request SCMessage
	 * @param callback
	 *            the callback
	 * @throws Exception
	 *             the exception
	 */
	public abstract void execute(SCMessage requestMsg, ISCMessageCallback callback) throws Exception;

	/**
	 * Execute.
	 * 
	 * @param requestMsg
	 *            the request SCMessage
	 * @param callback
	 *            the callback
	 * @param timeoutInSeconds
	 *            the timeout in seconds
	 * @throws Exception
	 *             the exception
	 */
	public abstract void execute(SCMessage requestMsg, ISCMessageCallback callback, int timeoutInSeconds)
			throws Exception;

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deleteSession() throws Exception;

	/**
	 * Delete session.
	 * 
	 * @param timeoutInSeconds
	 *            the timeout in seconds
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deleteSession(int timeoutInSeconds) throws Exception;

	/** {@inheritDoc} */
	@Override
	public abstract ServiceContext getServiceContext();

	/** {@inheritDoc} */
	@Override
	public abstract String getSessionId();

	public abstract void setSCResponseTimeMillis(int scResponseTimeMillis);

	public abstract int getSCResponseTimeMillis();
}
