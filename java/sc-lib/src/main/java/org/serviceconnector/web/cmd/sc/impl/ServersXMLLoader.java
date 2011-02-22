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
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.server.Server;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class ServersXMLLoader.
 */
public class ServersXMLLoader extends AbstractXMLLoader {

	/**
	 * Instantiates a new default xml loader.
	 */
	public ServersXMLLoader() {
	}

	/** {@inheritDoc} */
	@Override
	public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		ServerRegistry serverRegistry = AppContext.getServerRegistry();
		writer.writeStartElement("servers");
		Server[] servers = serverRegistry.getServers();
		for (Server server : servers) {
			writer.writeStartElement("server");
			this.writeBean(writer, server);
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

	@Override
	public IFactoryable newInstance() {
		return new ServersXMLLoader();
	}

}