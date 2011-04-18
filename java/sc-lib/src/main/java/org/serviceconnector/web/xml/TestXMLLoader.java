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

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.web.IWebRequest;

/**
 * The Class SessionsXMLLoader.
 */
public class TestXMLLoader extends AbstractXMLLoader {

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		String testParameter = request.getParameter("test");
		writer.writeStartElement("test");
		writer.writeAttribute("size", String.valueOf(1000));
		writer.writeAttribute("page", String.valueOf(1));
		writer.writeAttribute("last", String.valueOf(1000 / 50));
		writer.writeAttribute("pageSize", String.valueOf(50));
		for (int i = 0; i < 50; i++) {
			writer.writeStartElement("item");
			writer.writeAttribute("index", String.valueOf(i));
			writer.writeCharacters(String.valueOf(i));
			writer.writeEndElement();
		}
		writer.writeEndElement(); // close test tag
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(Writer writer, IWebRequest request) throws Exception {
		if (writer instanceof XMLStreamWriter) {
			this.loadBody((XMLStreamWriter) writer, request);
		}
	}
}