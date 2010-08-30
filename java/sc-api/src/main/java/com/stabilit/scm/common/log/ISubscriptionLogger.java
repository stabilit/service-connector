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
package com.stabilit.scm.common.log;

import com.stabilit.scm.common.scmp.SCMPMessage;

public interface ISubscriptionLogger {

	public abstract void logNoDataTimeout(String className, String sessionId);

	public abstract void logPoll(String className, String sessionId, SCMPMessage queueMessage, int queueSize);

	public abstract void logSubscribe(String className, SCMPMessage queueMessage, int queueSize);

	public abstract void logUnsubscribe(String className, int queueSize);
}
