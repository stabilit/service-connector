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
package org.serviceconnector.web.cmd.sc;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.SCVersion;
import org.serviceconnector.factory.Factory;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IXMLLoader;

/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class DefaultXMLLoaderFactory extends Factory {

	protected static DefaultXMLLoaderFactory loaderFactory = new DefaultXMLLoaderFactory();

	private DefaultXMLLoaderFactory() {
		IXMLLoader loader = new DefaultXMLLoader();
		this.add("default", loader);
		loader = new TimerXMLLoader();
		this.add("/timer", loader);
	}

	public static IXMLLoader getXMLLoader(String url) {
		IXMLLoader xmlLoader = (IXMLLoader) loaderFactory.getInstance(url);
		if (xmlLoader == null) {
			return (IXMLLoader) loaderFactory.getInstance("default");
		}
		return xmlLoader;
	}

	public static class DefaultXMLLoader extends AbstractXMLLoader {

		public DefaultXMLLoader() {
		}

		@Override
		public final void loadBody(XMLStreamWriter writer) throws Exception {
		}

	}

	public static class TimerXMLLoader extends AbstractXMLLoader {

		public TimerXMLLoader() {
		}

		@Override
		public void loadBody(XMLStreamWriter writer) throws Exception {
		}

	}
}
