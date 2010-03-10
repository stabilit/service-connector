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

import com.stabilit.sc.app.client.ISubscribe;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.msg.IClientListener;

/**
 * SubscribePublishService.
 * 
 * @author JTraber
 */
class SubscribePublishService extends Service implements ISubscribePublishService {

	// connection which handles subscription process, only used for making unsubscribe
	// never use it in other cases! Other operations should be done over new connections from ConnnectionPools
	private ISubscribe conn;
	private String subscribeId;

	/**
	 * Instantiates a new subscribePublish service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 */
	protected SubscribePublishService(String serviceName, Class<? extends IClientListener> serviceHandler,
			ClientApplicationContext ctx) {
		super(serviceName, serviceHandler, ctx);
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(SubscriptionMask subscriptionMask, int timeout) {
		conn = (ISubscribe) pool.lendConnection(ctx, this.clientListenerClass);
		try {
			subscribeId = conn.subscribe();
			conn.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} 
	 * @throws ServiceException */
	@Override
	public void unsubscribe(int timeout) throws ServiceException {
		if (conn == null)
			throw new ServiceException("Unsubscribe Service impossible first subscribe is necessary.");
		
		conn.stopSubscriptionActionOnConnection();
		ISubscribe conn = (ISubscribe) pool.lendConnection(ctx, this.clientListenerClass);
		conn.unsubscribe(subscribeId);
		conn.releaseConnection();
	}

	/** {@inheritDoc} 
	 * @throws ServiceException */
	@Override
	public void changeSubscription(SubscriptionMask newSubscriptionMask, int timeout) throws ServiceException {
		if (conn == null)
			throw new ServiceException("Change Subscription impossible first subscribe is necessary.");
	}
}
