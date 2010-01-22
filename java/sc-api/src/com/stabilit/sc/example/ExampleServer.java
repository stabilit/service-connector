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
package com.stabilit.sc.example;

import com.stabilit.sc.ProtocolType;
import com.stabilit.sc.ServerScConnection;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.handler.ServiceResponseHandler;
import com.stabilit.sc.handler.ServiceTimeoutHandler;
import com.stabilit.sc.msg.CompressionType;
import com.stabilit.sc.msg.IResponseMessage;
import com.stabilit.sc.msg.ResponseMessage;
import com.stabilit.sc.msg.RoutingInformation;
import com.stabilit.sc.service.ConnectionInformation;
import com.stabilit.sc.service.IPublishService;
import com.stabilit.sc.service.Service;
import com.stabilit.sc.service.SubscriptionMask;

/**
 * @author JTraber
 * 
 */
public class ExampleServer {

	public void runPublishService() {
		ServerScConnection sc = new ServerScConnection("host", 80, ProtocolType.HTTP, 10);

		try {
			sc.attach(10, 10, 10);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		}

		IPublishService publishService = sc.registerPublishService("serviceName",
				new ServiceResponseHandler() {

					@Override
					public void exceptionCaught(Service service) {
					}

					@Override
					public void messageReceived(Service service, IResponseMessage response) {

					}

				}, new ServiceTimeoutHandler() {

					@Override
					public void connectTimedOut(Service service) {

					}

					@Override
					public void readTimedOut(Service service) {

					}

					@Override
					public void writeTimedOut(Service service) {

					}

				});

		try {
			publishService.connect(10, new ConnectionInformation());
			publishService.publish(new ResponseMessage(new RoutingInformation(),
					CompressionType.NONE), new SubscriptionMask(), 10);
			publishService.disconnect(10);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		sc.detach(10);
	}
}
