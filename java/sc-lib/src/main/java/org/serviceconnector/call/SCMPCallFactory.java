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
package org.serviceconnector.call;

import org.apache.log4j.Logger;

/**
 * The Class SCMPCallFactory. Responsible for instantiating calls.
 * 
 * @author JTraber
 */
public final class SCMPCallFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPCallFactory.class);

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
	/** The REGISTER_SERVER_CALL. */
	public static final ISCMPCall REGISTER_SERVER_CALL = new SCMPRegisterServerCall();
	/** The DEREGISTER_SERVER_CALL. */
	public static final ISCMPCall DEREGISTER_SERVER_CALL = new SCMPDeRegisterServerCall();
	/** The CLN_CREATE_SESSION_CALL. */
	public static final ISCMPCall CLN_CREATE_SESSION_CALL = new SCMPClnCreateSessionCall();
	/** The CLN_DELETE_SESSION_CALL. */
	public static final ISCMPCall CLN_DELETE_SESSION_CALL = new SCMPClnDeleteSessionCall();
	/** The INSPECT_CALL. */
	public static final ISCMPCall INSPECT_CALL = new SCMPInspectCall();
	/** The MANAGE_CALL. */
	public static final ISCMPCall MANAGE_CALL = new SCMPManageCall();
	/** The CLN_EXECUTE_CALL. */
	public static final ISCMPCall CLN_EXECUTE_CALL = new SCMPClnExecuteCall();
	/** The ECHO_CALL. */
	public static final ISCMPCall ECHO_CALL = new SCMPEchoCall();

	/** The CLN_SUBSCRIBE_CALL. */
	public static final ISCMPCall CLN_SUBSCRIBE_CALL = new SCMPClnSubscribeCall();
	/** The Constant CLN_CHANGE_SUBSCRIPTION. */
	public static final ISCMPCall CLN_CHANGE_SUBSCRIPTION = new SCMPClnChangeSubscriptionCall();
	/** The CLN_UNSUBSCRIBE_CALL. */
	public static final ISCMPCall CLN_UNSUBSCRIBE_CALL = new SCMPClnUnsubscribeCall();
	/** The RECEIVE_PUBLICATION. */
	public static final ISCMPCall RECEIVE_PUBLICATION = new SCMPReceivePublicationCall();
	/** The UPLOAD_CALL. */
	public static final ISCMPCall FILE_UPLOAD_CALL = new SCMPFileUploadCall();
	/** The FILE_DOWNLOAD_CALL. */
	public static final ISCMPCall FILE_DOWNLOAD_CALL = new SCMPFileDownloadCall();
	/** The FILE_LIST_CALL. */
	public static final ISCMPCall FILE_LIST_CALL = new SCMPFileListCall();

	/********************** Calls from SC **********************/

	/** The SRV_CREATE_SESSION_CALL. */
	public static final ISCMPCall SRV_CREATE_SESSION_CALL = new SCMPSrvCreateSessionCall();
	/** The SRV_DELETE_SESSION_CALL. */
	public static final ISCMPCall SRV_DELETE_SESSION_CALL = new SCMPSrvDeleteSessionCall();
	/** The SRV_ABORT_SESSION. */
	public static final ISCMPCall SRV_ABORT_SESSION = new SCMPSrvAbortSessionCall();
	/** The Constant SRV_EXECUTE_CALL. */
	public static final ISCMPCall SRV_EXECUTE_CALL = new SCMPSrvExecuteCall();

	/** The SRV_SUBSCRIBE_CALL. */
	public static final ISCMPCall SRV_SUBSCRIBE_CALL = new SCMPSrvSubscribeCall();
	/** The SRV_CHANGE_SUBSCRIPTION_CALL. */
	public static final ISCMPCall SRV_CHANGE_SUBSCRIPTION_CALL = new SCMPSrvChangeSubscriptionCall();
	/** The SRV_UNSUBSCRIBE_CALL. */
	public static final ISCMPCall SRV_UNSUBSCRIBE_CALL = new SCMPSrvUnsubscribeCall();
	/** The PUBLISH_CALL. */
	public static final ISCMPCall PUBLISH_CALL = new SCMPPublishCall();
}
