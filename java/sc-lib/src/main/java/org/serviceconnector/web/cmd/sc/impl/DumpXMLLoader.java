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
import org.serviceconnector.util.DumpUtility;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class DumpXMLLoader.
 */
public class DumpXMLLoader extends AbstractXMLLoader {

	/**
	 * Instantiates a new default xml loader.
	 */
	public DumpXMLLoader() {
	}

	@Override
	public void loadBody(Writer writer, IWebRequest request) throws Exception {
		String name = request.getParameter("name");
		String dumpPath = AppContext.getBasicConfiguration().getDumpPath();
		DumpUtility.readDumpFileToWriter(dumpPath, name, writer);
		return;
	}

	@Override
	public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public boolean isText() {
		return true;
	}

	@Override
	public IFactoryable newInstance() {
		return new DumpXMLLoader();
	}
}