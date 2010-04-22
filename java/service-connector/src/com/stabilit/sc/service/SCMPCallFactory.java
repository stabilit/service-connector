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
package com.stabilit.sc.service;

import com.stabilit.sc.cln.service.ISCMPCall;
import com.stabilit.sc.cln.service.SCMPClnEchoCall;

/**
 * @author JTraber
 * 
 */
public class SCMPCallFactory {

	public static final ISCMPCall SRV_CREATE_SESSION_CALL = new SCMPSrvCreateSessionCall();
	public static final ISCMPCall SRV_DELETE_SESSION_CALL = new SCMPSrvDeleteSessionCall();
	public static final ISCMPCall CLN_ECHO_CALL = new SCMPClnEchoCall();
	public static final ISCMPCall SRV_ECHO_CALL = new SCMPSrvEchoCall();
	public static final ISCMPCall SRV_DATA_CALL = new SCMPSrvDataCall();
}
