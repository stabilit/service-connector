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

import com.stabilit.sc.ClientScConnection;
import com.stabilit.sc.ProtocolType;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.handler.ClientResponseHandler;
import com.stabilit.sc.handler.ClientTimeoutHandler;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.RoutingInformation;
import com.stabilit.sc.service.ConnectionInformation;
import com.stabilit.sc.service.IRequestResponseService;
import com.stabilit.sc.service.ISubscribePublishService;
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

		IRequestResponseService sendService = sc.newRequestResponseService("serviceName",
				new ClientResponseHandler() {

					@Override
					public void exceptionCaught(Service service, ScConnectionException exception) {
					}

					@Override
					public void messageReceived(Service service, IMessage response) {
					}

				}, new ClientTimeoutHandler() {

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
			sendService.send(new Message(new RoutingInformation(), ""), 12, false);

			IMessage response = sendService.sendAndReceive(
					new Message(new RoutingInformation(), ""), 12, false);
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

		ISubscribePublishService sendService = sc.newSubscribePublishService("serviceName",
				new ClientResponseHandler() {

					@Override
					public void exceptionCaught(Service service, ScConnectionException exception) {
					}

					@Override
					public void messageReceived(Service service, IMessage response) {
					}

				}, new ClientTimeoutHandler() {

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
