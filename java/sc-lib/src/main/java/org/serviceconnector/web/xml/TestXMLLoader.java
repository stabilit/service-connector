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
		String page = request.getParameter("page");
		String site = request.getParameter("site");
		int iPage = 1;
		int iSite = 1;
		if (page != null) {
			try {
				iPage = Integer.parseInt(page);
			} catch (Exception e) {
				iPage = 1;
			}
		}
		if (site != null) {
			try {
				iSite = Integer.parseInt(site);
			} catch (Exception e) {
				iSite = 1;
			}
		}
		String testParameter = request.getParameter("test");
		writer.writeStartElement("test");
		writer.writeAttribute("size", String.valueOf(1000));
		writer.writeAttribute("site", String.valueOf(iSite));
		writer.writeAttribute("page", String.valueOf(iPage));
		writer.writeAttribute("lastPage", String.valueOf(1000 / 25));
		writer.writeAttribute("lastSite", String.valueOf(1000 / 25 / 20));
		writer.writeAttribute("pageSize", String.valueOf(25));
		writer.writeAttribute("siteSize", String.valueOf(20));
		int begin = (iPage-1) * 25;
		int end = (iPage * 25);
		for (int i = begin; i < end; i++) {
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