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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.serviceconnector.factory.Factory;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IXMLLoader;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class DefaultXMLLoaderFactory extends Factory {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(DefaultXMLLoaderFactory.class);

	/** The loader factory. */
	protected static DefaultXMLLoaderFactory loaderFactory = new DefaultXMLLoaderFactory();

	/**
	 * Instantiates a new default xml loader factory.
	 */
	private DefaultXMLLoaderFactory() {
		IXMLLoader loader = new DefaultXMLLoader();
		this.add("default", loader);
		loader = new ServicesXMLLoader();
		this.add("/services", loader);
		loader = new TimerXMLLoader();
		this.add("/timer", loader);
	}

	/**
	 * Gets the xML loader.
	 * 
	 * @param url
	 *            the url
	 * @return the xML loader
	 */
	public static IXMLLoader getXMLLoader(String url) {
		if (url == null) {
			return (IXMLLoader) loaderFactory.getInstance("default");
		}
		int questionMarkPos = url.indexOf("?");
		if (questionMarkPos > 0) {
			url = url.substring(0, questionMarkPos);
		}
		IXMLLoader xmlLoader = (IXMLLoader) loaderFactory.getInstance(url);
		if (xmlLoader == null) {
			xmlLoader = (IXMLLoader) loaderFactory.getInstance("default");
		}
		if (xmlLoader == null) {
			return null;
		}
		return (IXMLLoader) xmlLoader.newInstance();
	}

	/**
	 * The Class DefaultXMLLoader.
	 */
	public static class DefaultXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public DefaultXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer) throws Exception {
			SystemInfo systemInfo = new SystemInfo();
			writer.writeStartElement("system");
			writer.writeStartElement("info");
			this.writeBean(writer, systemInfo);
			writer.writeEndElement(); // close info tag
			Properties properties = System.getProperties();
			writer.writeStartElement("properties");
			for (Entry<Object, Object> entry : properties.entrySet()) {
			   String name = (String) entry.getKey();
			   String value = (String) entry.getValue();
			   writer.writeStartElement(name);
			   writer.writeCData(value);
			   writer.writeEndElement();
			}
			writer.writeEndElement(); // close properties tag
			writer.writeEndElement(); // close system tag
			
		}

		@Override
		public IFactoryable newInstance() {
			return new DefaultXMLLoader();
		}

	}

	/**
	 * The Class ServicesXMLLoader.
	 */
	public static class ServicesXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public ServicesXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer) throws Exception {
			ServiceRegistry serviceRegistry = ServiceRegistry
					.getCurrentInstance();
			writer.writeStartElement("services");
			Service[] services = serviceRegistry.getServices();
			for (Service service : services) {
				writer.writeStartElement("service");
				this.writeBean(writer, service);
				writer.writeEndElement(); // close services tag
			}
			writer.writeEndElement(); // close services tag
		}

		@Override
		public IFactoryable newInstance() {
			return new ServicesXMLLoader();
		}

	}

	/**
	 * The Class TimerXMLLoader.
	 */
	public static class TimerXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new timer xml loader.
		 */
		public TimerXMLLoader() {
		}

		@Override
		public IFactoryable newInstance() {
			return new TimerXMLLoader();
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(XMLStreamWriter writer) throws Exception {
		}

	}
}
