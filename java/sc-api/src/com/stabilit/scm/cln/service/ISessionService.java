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
package com.stabilit.scm.cln.service;


/**
 * @author JTraber
 *
 */
public interface ISessionService {

	public abstract ISessionContext getSessionContext();
	
	public abstract void createSession(String sessionInfo, int echoTimeout, int echoInterval) throws Exception;

	public abstract SCMessage execute(SCMessage requestMsg)  throws Exception;
	
	public abstract void execute(SCMessage requestMsg, ISCMessageCallback callback)  throws Exception;

	public abstract void deleteSession() throws Exception;

}
