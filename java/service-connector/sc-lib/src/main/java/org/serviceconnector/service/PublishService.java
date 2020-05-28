/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.service;

import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.sc.SubscribeCommandCallback;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.util.XMLDumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides communication functions.
 */
public class PublishService extends StatefulService implements IPublishService {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(PublishService.class);

	/** The subscription queue. */
	private PublishMessageQueue<SCMPMessage> publishMessageQueue;

	/**
	 * Instantiates a new publish service.
	 *
	 * @param name the name
	 */
	public PublishService(String name) {
		super(name, ServiceType.PUBLISH_SERVICE);
		this.publishMessageQueue = new PublishMessageQueue<SCMPMessage>();
	}

	/**
	 * Gets the subscription queue.
	 *
	 * @return the subscription queue
	 */
	@Override
	public PublishMessageQueue<SCMPMessage> getMessageQueue() {
		return this.publishMessageQueue;
	}

	/**
	 * Allocate server and subscribe.
	 *
	 * @param msgToForward the msg to forward
	 * @param callback the callback
	 * @param subscription the subscription
	 * @param timeoutMillis the timeout millis
	 * @throws Exception the exception
	 */
	public synchronized void allocateServerAndSubscribe(SCMPMessage msgToForward, SubscribeCommandCallback callback, Subscription subscription, int timeoutMillis)
			throws Exception {
		int numberOfServer = this.listOfServers.size();
		if (numberOfServer == 0) {
			// no server registered for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SERVER, "service=" + msgToForward.getServiceName());
			scmpCommandException.setMessageType(msgToForward.getMessageType());
			throw scmpCommandException;
		}
		for (int i = 0; i < numberOfServer; i++) {
			this.serverIndex++;
			if (this.serverIndex >= numberOfServer) {
				// serverIndex reached the end of list no more servers
				this.serverIndex = 0;
			}
			StatefulServer server = this.listOfServers.get(serverIndex);
			if (server.hasFreeSession()) {
				server.addSession(subscription);
				subscription.setServer(server);
				try {
					server.subscribe(msgToForward, callback, timeoutMillis);
				} catch (Exception e) {
					server.removeSession(subscription);
					throw e;
				}
				return;
			}
		}
		// no free server available
		NoFreeServerException noFreeSessionExc = new NoFreeServerException(SCMPError.NO_FREE_SERVER, "service=" + msgToForward.getServiceName());
		noFreeSessionExc.setMessageType(msgToForward.getMessageType());
		throw noFreeSessionExc;
	}

	@Override
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("service");
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("type", this.type.getValue());
		writer.writeAttribute("enabled", this.enabled);
		writer.writeAttribute("serverIndex", this.serverIndex);
		StatefulServer[] servers = listOfServers.toArray(new StatefulServer[0]);
		for (StatefulServer statefulServer : servers) {
			statefulServer.dump(writer);
		}
		this.publishMessageQueue.dump(writer);
		writer.writeEndElement(); // service
	}
}
