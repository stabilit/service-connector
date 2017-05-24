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

import java.io.File;
import java.io.InputStream;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.WebUtil;

/**
 * The Class ResourceXMLLoader.
 */
public class ResourceXMLLoader extends AbstractXMLLoader {

	/** {@inheritDoc} */
	@Override
	public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		String name = request.getParameter("name");
		if (name == null) {
			return;
		}
		String path = name.replace(File.separatorChar, '/');
		InputStream is = WebUtil.loadResource(path);
		if (is == null) {
			this.addMeta("exception", "resource for name = " + name + " not found");
			return;
		}
		try {
			writer.writeStartElement("resource");
			writer.writeAttribute("name", name);
			writer.writeAttribute("path", path);
			writer.writeEndElement();
		} catch (Exception e) {
			this.addMeta("exception", e.toString());
			return;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
