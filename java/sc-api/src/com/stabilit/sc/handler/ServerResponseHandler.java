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
package com.stabilit.sc.handler;

import com.stabilit.sc.ServerScConnection;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.msg.IMessage;

/**
 * @author JTraber
 * 
 */
public interface ServerResponseHandler {

	public void controlMessageReceived(ServerScConnection connection, IMessage message);

	public void executionMessageReceived(ServerScConnection connection, IMessage message);

	public void exceptionCaught(ServerScConnection connection, ScConnectionException exception);
}
