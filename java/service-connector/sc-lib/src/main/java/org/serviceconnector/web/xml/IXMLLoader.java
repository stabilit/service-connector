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
package org.serviceconnector.web.xml;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.web.IWebRequest;

/**
 * The Interface IXMLLoader.
 */
public interface IXMLLoader {

	/**
	 * Checks if this loader returns raw text instead of xml.
	 *
	 * @return true, if is raw
	 */
	public abstract boolean isText();

	/**
	 * Load.
	 *
	 * @param request the request
	 * @param os the os
	 * @throws Exception the exception
	 */
	public abstract void load(IWebRequest request, OutputStream os) throws Exception;

	/**
	 * Load body.
	 *
	 * @param writer the writer
	 * @param request the request
	 * @throws Exception the exception
	 */
	public abstract void loadBody(Writer writer, IWebRequest request) throws Exception;

	/**
	 * Load body.
	 *
	 * @param writer the writer
	 * @param request the request
	 * @throws Exception the exception
	 */
	public abstract void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception;

	/**
	 * Adds the meta.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public abstract void addMeta(String name, String value);

	/**
	 * Adds the meta.
	 *
	 * @param map the map
	 */
	public abstract void addMeta(Map<String, String> map);

}
