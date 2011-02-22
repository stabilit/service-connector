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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.WebUtil;

/**
 * The Class AjaxResourceXMLLoader.
 */
public class AjaxResourceXMLLoader extends AbstractXMLLoader {
	/**
	 * Instantiates a new timer xml loader.
	 */
	public AjaxResourceXMLLoader() {
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new AjaxResourceXMLLoader();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isText() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void loadBody(Writer writer, IWebRequest request) throws Exception {
		String name = request.getParameter("name");
		InputStream is = WebUtil.loadResource(name);
		if (is == null) {
			this.addMeta("exception", "resource for name = " + name + " not found");
			return;
		}
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				writer.write(line);
				writer.write("<br/>");
			}
			writer.flush();
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