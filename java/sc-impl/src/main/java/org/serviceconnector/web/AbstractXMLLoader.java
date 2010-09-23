/*
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
 */
package org.serviceconnector.web;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.SCVersion;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.util.DateTimeUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractXMLLoader.
 */
public abstract class AbstractXMLLoader implements IXMLLoader {

	/**
	 * Instantiates a new abstract xml loader.
	 */
	public AbstractXMLLoader() {
	}
	
	@Override
	public IFactoryable newInstance() {
		return this;
	}
	/**
	 * Load body.
	 *
	 * @param writer the writer
	 */
	public abstract void loadBody(XMLStreamWriter writer) throws Exception;

	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IXMLLoader#load(java.io.OutputStream)
	 */
	@Override
	public final void load(IWebRequest request, OutputStream os) throws Exception {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(os);
		writer.writeStartDocument();
		writer.writeStartElement("sc-web");
		writer.writeStartElement("head");
		writer.writeStartElement("meta");
		writer.writeAttribute("creation", DateTimeUtility.getCurrentTimeZoneMillis());
		writer.writeEndElement(); // close meta tag
		writer.writeStartElement("meta");
		writer.writeAttribute("scversion", SCVersion.CURRENT.toString());			
		writer.writeEndElement(); // close meta tag
		writer.writeEndElement(); // close head tag
		writer.writeStartElement("body");
		this.loadBody(writer);
		writer.writeEndElement(); // close body tag
		writer.writeEndElement(); // close root tag sc-web
		writer.writeEndDocument();
		writer.close();			
	}
	
}
