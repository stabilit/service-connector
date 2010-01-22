/*
 *-----------------------------------------------------------------------------*
 *                            Copyright � 2010 by                              *
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

import com.stabilit.sc.ClientScConnection;
import com.stabilit.sc.ProtocolType;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.handler.ServiceResponseHandler;
import com.stabilit.sc.handler.ServiceTimeoutHandler;
import com.stabilit.sc.msg.CompressionType;
import com.stabilit.sc.msg.IResponseMessage;
import com.stabilit.sc.msg.RequestMessage;
import com.stabilit.sc.msg.RoutingInformation;
import com.stabilit.sc.service.ConnectionInformation;
import com.stabilit.sc.service.ISendService;
import com.stabilit.sc.service.ISubscribeService;
import com.stabilit.sc.service.Service;
import com.stabilit.sc.service.SubscriptionMask;

/**
 * @author JTraber
 * 
 */
public class ExampleClient {

	public void runSendService() {

		ClientScConnection sc = new ClientScConnection("localhost", 80, ProtocolType.HTTP, 3);
		try {
			sc.attach(10, 2, 12);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		}

		ISendService sendService = sc.newSendService("serviceName", new ServiceResponseHandler() {

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
			sendService.connect(10, new ConnectionInformation());
			sendService
					.send(new RequestMessage(new RoutingInformation(), CompressionType.NONE), 12);

			IResponseMessage response = sendService.sendAndReceive(new RequestMessage(
					new RoutingInformation(), CompressionType.NONE), 12);
			System.out.println(response);
			sendService.disconnect(10);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		sc.detach(10);
	}

	public void runSubscribeService() {

		ClientScConnection sc = new ClientScConnection("localhost", 80, ProtocolType.HTTP, 3);
		try {
			sc.attach(10, 2, 12);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		}

		ISubscribeService sendService = sc.newSubscribeService("serviceName",
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
			sendService.connect(10, new ConnectionInformation());
			sendService.subscribe(new SubscriptionMask(), 10);
			sendService.changeSubscription(new SubscriptionMask(), 10);
			sendService.unsubscribe(10);
			sendService.disconnect(10);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		sc.detach(10);
	}
}
