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
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.IEndpoint;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class RespondersXMLLoader.
 */
public class RespondersXMLLoader extends AbstractXMLLoader {

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
		writer.writeStartElement("responders");
		IResponder[] responders = responderRegistry.getResponders();
		for (IResponder responder : responders) {
			writer.writeStartElement("responder");
			writer.writeStartElement("responderConfig");
			this.writeBean(writer, responder.getListenerConfig());
			writer.writeEndElement();
			writer.writeStartElement("endPoints");
			List<IEndpoint> endPointList = responder.getEndpoints();
			for (IEndpoint endPoint : endPointList) {
				writer.writeStartElement("endPoint");
				this.writeBean(writer, endPoint);
				writer.writeEndElement();
			}
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeEndElement(); // close sessions tag
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(Writer writer, IWebRequest request) throws Exception {
		if (writer instanceof XMLStreamWriter) {
			this.loadBody((XMLStreamWriter) writer, request);
		}
	}
}