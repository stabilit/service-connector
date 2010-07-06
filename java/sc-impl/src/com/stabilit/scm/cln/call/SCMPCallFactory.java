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
package com.stabilit.scm.cln.call;

/**
 * The Class SCMPCallFactory. Responsible for instantiating calls.
 * 
 * @author JTraber
 */
public final class SCMPCallFactory {

	/**
	 * Instantiates a new SCMPCallFactory.
	 */
	private SCMPCallFactory() {
	}

	/********************** Calls to SC **********************/

	/** The ATTACH_CALL. */
	public static final ISCMPCall ATTACH_CALL = new SCMPAttachCall();
	/** The DETACH_CALL. */
	public static final ISCMPCall DETACH_CALL = new SCMPDetachCall();
	/** The REGISTER_SERVICE_CALL. */
	public static final ISCMPCall REGISTER_SERVICE_CALL = new SCMPRegisterServiceCall();
	/** The DEREGISTER_SERVICE_CALL. */
	public static final ISCMPCall DEREGISTER_SERVICE_CALL = new SCMPDeRegisterServiceCall();
	/** The CLN_CREATE_SESSION_CALL. */
	public static final ISCMPCall CLN_CREATE_SESSION_CALL = new SCMPClnCreateSessionCall();
	/** The CLN_DELETE_SESSION_CALL. */
	public static final ISCMPCall CLN_DELETE_SESSION_CALL = new SCMPClnDeleteSessionCall();
	/** The INSPECT_CALL. */
	public static final ISCMPCall INSPECT_CALL = new SCMPInspectCall();
	/** The CLN_DATA_CALL. */
	public static final ISCMPCall CLN_DATA_CALL = new SCMPClnDataCall();
	/** The CLN_ECHO_CALL. */
	public static final ISCMPCall CLN_ECHO_CALL = new SCMPClnEchoCall();
	/** The ECHO_SC_CALL. */
	public static final ISCMPCall ECHO_SC_CALL = new SCMPEchoSCCall();
	/** The CLN_SYSTEM_CALL. */
	public static final ISCMPCall CLN_SYSTEM_CALL = new SCMPClnSystemCall();

	/********************** Calls from SC **********************/

	/** The Constant SRV_CREATE_SESSION_CALL. */
	public static final ISCMPCall SRV_CREATE_SESSION_CALL = new SCMPSrvCreateSessionCall();
	/** The Constant SRV_DELETE_SESSION_CALL. */
	public static final ISCMPCall SRV_DELETE_SESSION_CALL = new SCMPSrvDeleteSessionCall();
	/** The Constant SRV_ECHO_CALL. */
	public static final ISCMPCall SRV_ECHO_CALL = new SCMPSrvEchoCall();
	/** The Constant SRV_DATA_CALL. */
	public static final ISCMPCall SRV_DATA_CALL = new SCMPSrvDataCall();
	/** The Constant SRV_SYSTEM_CALL. */
	public static final ISCMPCall SRV_SYSTEM_CALL = new SCMPSrvSystemCall();

	public static final ISCMPCall CLN_SUBSCRIBE_CALL = new SCMPClnSubscribeCall();
	public static final ISCMPCall CLN_UNSUBSCRIBE_CALL = new SCMPClnUnsubscribeCall();
	public static final ISCMPCall SRV_SUBSCRIBE_CALL = new SCMPSrvSubscribeCall();
	public static final ISCMPCall SRV_UNSUBSCRIBE_CALL = new SCMPSrvUnsubscribeCall();
	public static final ISCMPCall PUBLISH_CALL = new SCMPPublishCall();
}
