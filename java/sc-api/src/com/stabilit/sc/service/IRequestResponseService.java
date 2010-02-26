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

import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IData;

/**
 * The Interface IRequestResponseService represents RequestResponseService.
 * 
 * @author JTraber
 */
public interface IRequestResponseService extends IService {

	/**
	 * Sends a message asynchronous.
	 * 
	 * @param message
	 *            the message
	 * @param timeout
	 *            the timeout
	 * @param compression
	 *            the compression
	 * 
	 * @throws ServiceException
	 *             exception in sending process
	 */
	void send(SCMP scmp, int timeout, boolean compression) throws ServiceException;

	/**
	 * Sends and receives message synchronous.
	 * 
	 * @param message
	 *            the message
	 * @param timeout
	 *            the timeout
	 * @param compression
	 *            the compression
	 * 
	 * @return the i message
	 * 
	 * @throws ServiceException
	 *             exception in send and receive process
	 */
	SCMP sendAndReceive(SCMP scmp, int timeout, boolean compression) throws ServiceException;
}
