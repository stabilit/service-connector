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
package com.stabilit.sc.handler;

import com.stabilit.sc.service.Service;

/**
 * The Interface ClientTimeoutHandler.
 * 
 * @author JTraber
 */
public interface ClientTimeoutHandler {

	/**
	 * Invoked when time run out in write process.
	 * 
	 * @param service
	 *            the service
	 */
	void writeTimedOut(Service service);

	/**
	 * Invoked when time run out in read process.
	 * 
	 * @param service
	 *            the service
	 */
	void readTimedOut(Service service);

	/**
	 * Invoked when time run out in connect process.
	 * 
	 * @param service
	 *            the service
	 */
	void connectTimedOut(Service service);
}
