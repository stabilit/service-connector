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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
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
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.NotFoundException;

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
		loader = new ResourceXMLLoader();
		this.add("/resource", loader);
		loader = new AjaxResourceXMLLoader();
		this.add("/ajax/resource", loader);
		loader = new TimerXMLLoader();
		this.add("/ajax/timer", loader);
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
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
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
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
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
	 * The Class ResourceXMLLoader.
	 */
	public static class ResourceXMLLoader extends AbstractXMLLoader {
		/**
		 * Instantiates a new timer xml loader.
		 */
		public ResourceXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public IFactoryable newInstance() {
			return new ResourceXMLLoader();
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String name = request.getParameter("name");
			InputStream is = null;
			try {
				is = ClassLoader.getSystemResourceAsStream(name);
				if (is == null) {
				   is = this.getClass().getResourceAsStream(name);
				}
				if (is == null) {
					is = new FileInputStream(name);
				}
				if (is == null) {
					this.addMeta("exception", "not found");
					return;
				}
				writer.writeStartElement("resource");
				writer.writeAttribute("name", name);
				writer.writeEndElement();
			} catch(Exception e) {
				this.addMeta("exception", e.toString());
				return;
			} finally {
				if (is != null) {
				   is.close();
				}
			}
		}

	}

	/**
	 * The Class AjaxResourceXMLLoader.
	 */
	public static class AjaxResourceXMLLoader extends AbstractXMLLoader {
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
			InputStream is = null;
			try {
				is = ClassLoader.getSystemResourceAsStream(name);
				if (is == null) {
				   is = this.getClass().getResourceAsStream(name);
				}
				if (is == null) {
					is = new FileInputStream(name);
				}
				if (is == null) {
					throw new NotFoundException();
				}
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
			} catch(Exception e) {
				return;
			} finally {
				if (is != null) {
				   is.close();
				}
			}
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
		public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
		}

	}
}
