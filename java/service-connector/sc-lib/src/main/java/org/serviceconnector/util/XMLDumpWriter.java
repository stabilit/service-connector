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
package org.serviceconnector.util;

import java.io.FileOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.SCVersion;

/**
 * The Class XMLDumpWriter is a wrapper for XMLStreamWriter.
 */
public class XMLDumpWriter {

	/** The writer. */
	private XMLStreamWriter writer;

	/**
	 * Instantiates a new XML dump writer.
	 *
	 * @param fos the fos
	 * @throws XMLStreamException the xML stream exception
	 */
	public XMLDumpWriter(FileOutputStream fos) throws XMLStreamException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		this.writer = factory.createXMLStreamWriter(fos);
	}

	/**
	 * Starts new document. writes the encoding, sc version and current timestamp
	 *
	 * @throws XMLStreamException the xML stream exception
	 */
	public void startDocument() throws XMLStreamException {
		writer.writeStartDocument();
		this.writeComment(" SC version=" + SCVersion.CURRENT.toString() + " ");
		this.writeComment(" Dump created at=" + DateTimeUtility.getCurrentTimeZoneMillis() + " ");
	}

	/**
	 * Write comment.
	 *
	 * @param comment the comment
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeComment(String comment) throws XMLStreamException {
		writer.writeComment(comment);
	}

	/**
	 * write the end tag to the document and flush it.
	 *
	 * @throws XMLStreamException the xML stream exception
	 */
	public void endDocument() throws XMLStreamException {
		writer.writeEndDocument();
		writer.flush();
	}

	/**
	 * Starts a new element.
	 *
	 * @param value the value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeStartElement(String value) throws XMLStreamException {
		writer.writeStartElement(value);
	}

	/**
	 * Closes the actual element.
	 *
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeEndElement() throws XMLStreamException {
		writer.writeEndElement();
	}

	/**
	 * Write element.
	 *
	 * @param name the name
	 * @param value the value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeElement(String name, String value) throws XMLStreamException {
		writer.writeStartElement(name);
		if (value != null) {
			writer.writeCharacters(value);
		}
		writer.writeEndElement();
	}

	/**
	 * Write element converting int value to String.
	 *
	 * @param name the name
	 * @param value the int value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeElement(String name, int value) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeCharacters(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write element converting long value to String.
	 *
	 * @param name the name
	 * @param value the long value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeElement(String name, long value) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeCharacters(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write element converting double value to String.
	 *
	 * @param name the name
	 * @param value the double value
	 * @throws Exception the exception
	 */
	public void writeElement(String name, double value) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeCharacters(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write element converting boolean value to String.
	 *
	 * @param name the name
	 * @param value the value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeElement(String name, boolean value) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeCharacters(String.valueOf(value));
		writer.writeEndElement();
	}

	/**
	 * Write string attribute value.
	 *
	 * @param attributeName the name of the attribute
	 * @param value the attribute value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeAttribute(String attributeName, String value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(attributeName, value);
		}
	}

	/**
	 * Write boolean attribute value converting it to string.
	 *
	 * @param attributeName the name of the attribute
	 * @param value the attribute value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeAttribute(String attributeName, Boolean value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(attributeName, String.valueOf(value));
		}
	}

	/**
	 * Write int attribute value converting it to string.
	 *
	 * @param attributeName the name of the attribute
	 * @param value the attribute value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeAttribute(String attributeName, int value) throws XMLStreamException {
		writer.writeAttribute(attributeName, String.valueOf(value));
	}

	/**
	 * Write long attribute value converting it to string.
	 *
	 * @param attributeName the name of the attribute
	 * @param value the attribute value
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeAttribute(String attributeName, Long value) throws XMLStreamException {
		if (value != null) {
			writer.writeAttribute(attributeName, String.valueOf(value));
		}
	}

	/**
	 * Write double attribute value converting it to string
	 *
	 * @param attributeName the name of the attribute
	 * @param value the attribute value
	 * @throws Exception the exception
	 */
	public void writeAttribute(String attributeName, double value) throws XMLStreamException {
		writer.writeAttribute(attributeName, String.valueOf(value));
	}
}
