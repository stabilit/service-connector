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
package com.stabilit.sc.cln.service;

/**
 * @author JTraber
 * 
 */
public class SCMPCallFactory {

	public static final ISCMPCall CONNECT_CALL = new SCMPConnectCall();
	public static final ISCMPCall DISCONNECT_CALL = new SCMPDisconnectCall();
	public static final ISCMPCall REGISTER_SERVICE_CALL = new SCMPRegisterServiceCall();
	public static final ISCMPCall DEREGISTER_SERVICE_CALL = new SCMPDeRegisterServiceCall();
	public static final ISCMPCall CLN_CREATE_SESSION_CALL = new SCMPClnCreateSessionCall();
	public static final ISCMPCall CLN_DELETE_SESSION_CALL = new SCMPClnDeleteSessionCall();
	public static final ISCMPCall INSPECT_CALL = new SCMPInspectCall();
	public static final ISCMPCall CLN_DATA_CALL = new SCMPClnDataCall();
	public static final ISCMPCall CLN_ECHO_CALL = new SCMPClnEchoCall();
	public static final ISCMPCall ECHO_SC_CALL = new SCMPEchoSCCall();
	public static final ISCMPCall CLN_SYSTEM_CALL = new SCMPClnSystemCall();
}
