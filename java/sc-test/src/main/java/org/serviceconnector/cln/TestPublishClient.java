package org.serviceconnector.cln;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestPublishServiceMessageCallback;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCPublishService;

public class TestPublishClient extends TestAbstractClient {

	private int noDataIntervalSeconds;
	private SCPublishService service;
	private TestPublishServiceMessageCallback callback;
	private SCSubscribeMessage scSubscribeMessage = new SCSubscribeMessage();
	private String sessionId;

	public TestPublishClient() {
		scSubscribeMessage.setMask(TestConstants.mask);
		scSubscribeMessage.setNoDataIntervalSeconds(50);
	}

	static {
		TestAbstractClient.LOGGER = Logger.getLogger(TestPublishClient.class);
	}

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] client name<br>
	 *            [1] SC host<br>
	 *            [2] SC port<br>
	 *            [3] connectionType ("netty.tcp" or "netty.http")<br>
	 *            [4] maxConnections<br>
	 *            [5] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [6] serviceName<br>
	 *            [7] echoIntervalSeconds<br>
	 *            [8] echoTimeoutSeconds<br>
	 *            [9] noDataIntervalSeconds<br>
	 *            [10] methodsToInvoke
	 */
	public static void main(String[] args) throws Exception {
		LOGGER.log(Level.OFF, "TestPublishClient is starting ...");
		for (int i = 0; i < args.length; i++) {
			LOGGER.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestPublishClient testClient = new TestPublishClient();
		testClient.setClientName(args[0]);
		testClient.setHost(args[1]);
		testClient.setPort(Integer.parseInt(args[2]));
		testClient.setConnectionType(args[3]);
		testClient.setMaxConnections(Integer.parseInt(args[4]));
		testClient.setKeepAliveIntervalSeconds(Integer.parseInt(args[5]));
		testClient.setServiceName(args[6]);
		// args[7], args[8] can be ignored in publish client (echoInterval, echoTimeout)
		// testClient.setEchoIntervalSeconds(Integer.parseInt(args[7]));
		// testClient.setEchoTimeoutSeconds(Integer.parseInt(args[8]));
		testClient.setNoDataIntervalSeconds(Integer.parseInt(args[9]));
		testClient.setMethodsToInvoke(Arrays.asList(args[10].split("\\|")));
		testClient.run();
	}

	public void setNoDataIntervalSeconds(int noDataIntervalSeconds) {
		this.noDataIntervalSeconds = noDataIntervalSeconds;
	}

	public void p_subscribe() throws Exception {
		service = client.newPublishService(this.serviceName);
		callback = new TestPublishServiceMessageCallback(service);
		service.subscribe(this.scSubscribeMessage, callback);
	}

	public void p_unsubscribeAfter10000() throws Exception {
		// 10000 message or 650 seconds
		this.waitForMessages(10000, 650000);
		service.unsubscribe();
	}

	public void p_unsubscribeAfter500() throws Exception {
		// 500 message or 10 seconds
		this.waitForMessages(500, 10000);
		service.unsubscribe();
	}

	public void p_unsubscribe() throws Exception {
		service.unsubscribe();
	}

	private void p_changeSubscriptionAfter500() throws Exception {
		// 500 message or 10 seconds
		this.waitForMessages(500, 10000);
		SCSubscribeMessage scSubscribeMessage = new SCSubscribeMessage();
		scSubscribeMessage.setMask(TestConstants.noRecvMask);
		service.changeSubscription(scSubscribeMessage);
		// sleep 10ms - a RCP with a old message might be on the way
		Thread.sleep(10);
		// reset received message counter
		TestPublishServiceMessageCallback.receivedMsg = 0;
		Thread.sleep(1000);
		if (TestPublishServiceMessageCallback.receivedMsg != 0) {
			LOGGER.error(this.clientName + " received messages " + TestPublishServiceMessageCallback.receivedMsg
					+ " but changed subscription with " + scSubscribeMessage.getMask());
		}
		scSubscribeMessage.setMask(TestConstants.mask);
		service.changeSubscription(scSubscribeMessage);
	}

	private void p_changeSubscription10000() throws Exception {
		SCSubscribeMessage scSubscribeMessage = new SCSubscribeMessage();

		for (int i = 0; i < 10000; i++) {
			if (i % 2 == 0) {
				scSubscribeMessage.setMask(TestConstants.noRecvMask);
			} else {
				scSubscribeMessage.setMask(TestConstants.mask1);
			}
			service.changeSubscription(scSubscribeMessage);
		}
	}

	public void f_subscribeReceive10000Unsubscribe() throws Exception {
		this.p_initAttach();
		this.scSubscribeMessage.setMask(TestConstants.mask);
		this.scSubscribeMessage.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		this.scSubscribeMessage.setData("10000");
		this.scSubscribeMessage.setNoDataIntervalSeconds(this.noDataIntervalSeconds);
		this.p_subscribe();
		this.p_unsubscribeAfter10000();
		this.p_detach();
		this.p_exit();
	}

	public void f_subscribeReceive500Unsubscribe() throws Exception {
		this.p_initAttach();
		this.scSubscribeMessage.setSessionInfo(TestConstants.publishCompressedMsgCmd);
		this.scSubscribeMessage.setData("500");
		this.scSubscribeMessage.setNoDataIntervalSeconds(this.noDataIntervalSeconds);
		this.p_subscribe();
		this.p_unsubscribeAfter500();
		this.p_detach();
		this.p_exit();
	}

	public void f_subscribeReceive500ChangeSubscriptionUnsubscribe() throws Exception {
		this.p_initAttach();
		this.p_subscribe();
		this.p_changeSubscriptionAfter500();
		this.p_unsubscribeAfter500();
		this.p_detach();
		this.p_exit();
	}

	public void f_10000ChangeSubscription() throws Exception {
		this.p_initAttach();
		this.p_subscribe();
		this.p_changeSubscription10000();
		this.p_unsubscribe();
		this.p_detach();
		this.p_exit();
	}

	public void f_subscribeReceive20_12SecUnsubscribe() throws Exception {
		this.p_initAttach();
		this.scSubscribeMessage.setSessionInfo(TestConstants.publishMsgWithDelayCmd);
		this.scSubscribeMessage.setData("20|12000");
		this.scSubscribeMessage.setNoDataIntervalSeconds(this.noDataIntervalSeconds);
		this.p_subscribe();
		// 20 message or 12 seconds
		this.waitForMessages(20, 250000);
		service.unsubscribe();
		this.p_detach();
		this.p_exit();
	}

	public void f_subscribeUnsubscribe() throws Exception {
		this.p_initAttach();
		this.p_subscribe();
		this.p_unsubscribeAfter10000();
		this.p_detach();
		this.p_exit();
	}

	private void waitForMessages(int numberOfMessages, int maxTimeMillis) throws Exception {
		long start = System.currentTimeMillis();

		while (TestPublishServiceMessageCallback.receivedMsg < numberOfMessages) {
			Thread.sleep(200);
			if ((System.currentTimeMillis() - start) > maxTimeMillis) {
				LOGGER.error(this.clientName + " sid=" + this.sessionId + "could not receive " + numberOfMessages + " messages in "
						+ maxTimeMillis + " milliseconds received: " + TestPublishServiceMessageCallback.receivedMsg + ".");
				break;
			}
		}
	}
}
