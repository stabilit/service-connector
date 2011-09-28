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

package org.serviceconnector.web.xml;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.SessionService;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.util.LinkedNode;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class ServicesXMLLoader.
 */
public class ServicesXMLLoader extends AbstractXMLLoader {

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		writer.writeStartElement("services");
		Service[] services = serviceRegistry.getServices();
		String serviceParameter = request.getParameter("service");
		int simulation = this.getParameterInt(request, "sim", 0);
		if (simulation > 0) {
			Service[] sim = new Service[simulation + services.length];
			System.arraycopy(services, 0, sim, 0, services.length);
			for (int i = services.length; i < simulation + services.length; i++) {
				sim[i] = new SessionService("sim " + i);
			}
			services = sim;
		}
		Paging paging = this.writePagingAttributes(writer, request, services.length, "");
		// String showSessionsParameter = request.getParameter("showsessions");
		int startIndex = paging.getStartIndex();
		int endIndex = paging.getEndIndex();
		for (int i = startIndex; i < endIndex; i++) {
			Service service = services[i];
			writer.writeStartElement("service");
			this.writeBean(writer, service);
			if (service instanceof IPublishService) {
				IPublishService publishService = (IPublishService) service;
				PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
				writer.writeStartElement("publishMessageQueueSize");
				writer.writeCData(String.valueOf(publishMessageQueue.getTotalSize() + simulation));
				writer.writeEndElement(); // end of publishMessageQueueSize
				writer.writeStartElement("publishMessageQueueReferencedNodeCount");
				writer.writeCData(String.valueOf(publishMessageQueue.getReferencedNodesCount() + simulation));
				writer.writeEndElement(); // end of publishMessageQueueReferencedNodeCount
			}
			if (service.getName().equals(serviceParameter)) {
				// take a look into
				writer.writeStartElement("details");
				if (service instanceof StatefulService) {
					List<StatefulServer> serverList = ((StatefulService) service).getServerList();
					writer.writeStartElement("servers");
					for (StatefulServer server : serverList) {
						writer.writeStartElement("server");
						this.writeBean(writer, server);
						writer.writeEndElement(); // close server tag
					}
					writer.writeEndElement(); // close servers tag
				}
				if (service instanceof IPublishService) {
					IPublishService publishService = (IPublishService) service;
					PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
					writer.writeStartElement("publishMessageQueue");
					Iterator<LinkedNode<SCMPMessage>> sqIter = publishMessageQueue.nodeIterator();
					while (sqIter.hasNext()) {
						LinkedNode<SCMPMessage> node = sqIter.next();
						SCMPMessage scmpMessage = node.getValue();
						writer.writeStartElement("scmpMessage");
						writer.writeStartElement("header");
						Map<String, String> header = scmpMessage.getHeader();
						for (Entry<?, ?> headerEntry : header.entrySet()) {
							writer.writeStartElement((String) headerEntry.getKey());
							Object value = headerEntry.getValue();
							if (value != null) {
								writer.writeCData(value.toString());
							}
							writer.writeEndElement();
						}
						writer.writeEndElement(); // end of header
						writer.writeStartElement("references");
						writer.writeCData(String.valueOf(node.getReferenceCount()));
						writer.writeEndElement(); // end of references
						writer.writeEndElement(); // end of scmpMessage
					}
					for (int j = 0; j < simulation; j++) {
						SCMPMessage scmpMessage = new SCMPMessage();
						scmpMessage.setHeader(SCMPHeaderAttributeKey.MASK, "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						scmpMessage.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, j);
						writer.writeStartElement("scmpMessage");
						writer.writeStartElement("header");
						Map<String, String> header = scmpMessage.getHeader();
						for (Entry<?, ?> headerEntry : header.entrySet()) {
							writer.writeStartElement((String) headerEntry.getKey());
							Object value = headerEntry.getValue();
							if (value != null) {
								writer.writeCData(value.toString());
							}
							writer.writeEndElement();
						}
						writer.writeEndElement(); // end of header
						writer.writeEndElement(); // end of scmpMessage
					}
					writer.writeEndElement(); // end of publishMessageQueue
				}
				writer.writeEndElement(); // end details tag
			}
			writer.writeEndElement(); // end service tag
		}
		writer.writeEndElement(); // close services tag
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(Writer writer, IWebRequest request) throws Exception {
		if (writer instanceof XMLStreamWriter) {
			this.loadBody((XMLStreamWriter) writer, request);
		}
	}
}