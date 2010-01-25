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
package com.stabilit.sc.example;

import com.stabilit.sc.ClientScConnection;
import com.stabilit.sc.ProtocolType;
import com.stabilit.sc.exception.ScConnectionException;
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
 * Example Client.
 * 
 * @author JTraber
 */
public class ExampleClient {

	/** The Constant KEEP_ALIVE_TIMEOUT. */
	private static final int KEEP_ALIVE_TIMEOUT = 12;

	/** The Constant KEEP_ALIVE_INTERVAL. */
	private static final int KEEP_ALIVE_INTERVAL = 2;

	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 10;

	/** The Constant NUM_OF_CON. */
	private static final int NUM_OF_CON = 3;

	/** The Constant PORT. */
	private static final int PORT = 80;

	/** The Constant HOST. */
	private static final String HOST = "localhost";

	/**
	 * Run requestResponse service.
	 */
	public void runrequestResponseService() {

		ClientScConnection sc = new ClientScConnection(HOST, PORT, ProtocolType.HTTP, NUM_OF_CON);
		try {
			sc.attach(TIMEOUT, KEEP_ALIVE_INTERVAL, KEEP_ALIVE_TIMEOUT);
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
			sendService.connect(TIMEOUT, new ConnectionInformation());
			sendService.send(new Message(new RoutingInformation(), ""), TIMEOUT, false);

			IMessage response = sendService.sendAndReceive(new Message(new RoutingInformation(), ""),
					TIMEOUT, false);
			System.out.println(response);
			sendService.disconnect(TIMEOUT);
			sc.detach(TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Run subscribe service.
	 */
	public void runSubscribeService() {

		ClientScConnection sc = new ClientScConnection(HOST, PORT, ProtocolType.HTTP, NUM_OF_CON);
		try {
			sc.attach(TIMEOUT, KEEP_ALIVE_INTERVAL, KEEP_ALIVE_TIMEOUT);
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
			sendService.connect(TIMEOUT, new ConnectionInformation());
			sendService.subscribe(new SubscriptionMask(), TIMEOUT);
			sendService.changeSubscription(new SubscriptionMask(), TIMEOUT);
			sendService.unsubscribe(TIMEOUT);
			sendService.disconnect(TIMEOUT);
			sc.detach(TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
