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
package com.stabilit.sc.service;

import com.stabilit.sc.exception.ScConnectionException;

/**
 * The Interface IService.
 * 
 * @author JTraber
 */
public interface IService {

	/**
	 * Connects to Sc.
	 * 
	 * @param timeout
	 *            the timeout
	 * @param connectionInformation
	 *            the connection information
	 * 
	 * @throws ScConnectionException
	 *             exception in connection process
	 */
	void connect(int timeout, ConnectionInformation connectionInformation) throws ScConnectionException;

	/*
	 * disconnect to service
	 */
	/**
	 * Disconnect from Sc.
	 * 
	 * @param timeout
	 *            the timeout
	 * 
	 * @throws ScConnectionException
	 *             exception in disconnection process
	 */
	void disconnect(int timeout) throws ScConnectionException;
}
