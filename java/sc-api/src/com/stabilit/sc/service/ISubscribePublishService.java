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

/**
 * The Interface ISubscribePublishService represents SubscribePublishService.
 * 
 * @author JTraber
 */
public interface ISubscribePublishService extends IService {

	/**
	 * Subscribe to Services.
	 * 
	 * @param subscriptionMask
	 *            the subscription mask
	 * @param timeout
	 *            the timeout
	 * 
	 * @throws ServiceException
	 *             exception in subscribe process
	 */
	void subscribe(SubscriptionMask subscriptionMask, int timeout) throws ServiceException;

	/**
	 * Unsubscribe from services.
	 * 
	 * @param timeout
	 *            the timeout
	 * 
	 * @throws ServiceException
	 *             exception in unsubscribe process
	 */
	void unsubscribe(int timeout) throws ServiceException;

	/**
	 * Change subscription.
	 * 
	 * @param newSubscriptionMask
	 *            the new subscription mask
	 * @param timeout
	 *            the timeout
	 * 
	 * @throws ServiceException
	 *             exception in changeSubscription process
	 */
	void changeSubscription(SubscriptionMask newSubscriptionMask, int timeout) throws ServiceException;
}
