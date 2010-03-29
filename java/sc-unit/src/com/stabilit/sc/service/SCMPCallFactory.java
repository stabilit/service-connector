/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.service;

/**
 * @author JTraber
 * 
 */
public class SCMPCallFactory {

	public static final ISCMPCall CONNECT_CALL = new SCMPConnectCall();
	public static final ISCMPCall DISCONNECT_CALL = new SCMPDisconnectCall();
	public static final ISCMPCall REGISTER_SERVICE_CALL = new SCMPRegisterServiceCall();
	public static final ISCMPCall DEREGISTER_SERVICE_CALL = new SCMPDeRegisterServiceCall();
	public static final ISCMPCall CREATE_SESSION_CALL = new SCMPCreateSessionCall();
	public static final ISCMPCall DELETE_SESSION_CALL = new SCMPDeleteSessionCall();
	public static final ISCMPCall MAINTENANCE_CALL = new SCMPMaintenanceCall();
	public static final ISCMPCall CLN_DATA_CALL = new SCMPClnDataCall();
	public static final ISCMPCall ALLOCATE_SESSION_CALL = new SCMPAllocateSessionCall();
	public static final ISCMPCall DEALLOCATE_SESSION_CALL = new SCMPDeAllocateSessionCall();
}
