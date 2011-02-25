/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */

package org.serviceconnector.web.cmd.sc.impl;

import java.io.Writer;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.Server;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class SubscriptionsXMLLoader.
 */
public class SubscriptionsXMLLoader extends AbstractXMLLoader {

	/**
	 * Instantiates a new default xml loader.
	 */
	public SubscriptionsXMLLoader() {
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		String serverParameter = request.getParameter("server");
		if (serverParameter != null) {
			ServerRegistry serverRegistry = AppContext.getServerRegistry();
			Server server = serverRegistry.getServer(serverParameter);
			if (server != null) {
				writer.writeStartElement("server");
				this.writeBean(writer, server);
				writer.writeEndElement();
			}
		}
		String serviceParameter = request.getParameter("service");
		if (serviceParameter != null) {
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			Service service = serviceRegistry.getService(serviceParameter);
			if (service != null) {
				writer.writeStartElement("service");
				if (service instanceof IPublishService) {
					IPublishService publishService = (IPublishService) service;
					PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
					writer.writeStartElement("publishMessageQueueSize");
					writer.writeCData(String.valueOf(publishMessageQueue.getSize()));
					writer.writeEndElement(); // end of publishMessageQueueSize
				}
				this.writeBean(writer, service);
				writer.writeEndElement();
			}
		}
		SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
		writer.writeStartElement("subscriptions");
		Subscription[] subscriptions = subscriptionRegistry.getSubscriptions();
		for (Subscription subscription : subscriptions) {
			writer.writeStartElement("subscription");
			this.writeBean(writer, subscription);
			writer.writeEndElement();
		}
		writer.writeEndElement(); // close subscriptions tag
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(Writer writer, IWebRequest request) throws Exception {
		if (writer instanceof XMLStreamWriter) {
			this.loadBody((XMLStreamWriter) writer, request);
		}
	}

	@Override
	public IFactoryable newInstance() {
		return new SubscriptionsXMLLoader();
	}

}