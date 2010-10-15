/*
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSessionService;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.ServiceType;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.SessionServer;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.InvalidParameterException;
import org.serviceconnector.web.NotFoundException;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.ctx.WebContext;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class DefaultXMLLoaderFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DefaultXMLLoaderFactory.class);

	/** The loader factory. */
	protected static DefaultXMLLoaderFactory loaderFactory = new DefaultXMLLoaderFactory();

	private Map<String, IXMLLoader> loaderMap = new ConcurrentHashMap<String, IXMLLoader>();

	/**
	 * Instantiates a new default xml loader factory.
	 */
	public DefaultXMLLoaderFactory() {
		IXMLLoader loader = new DefaultXMLLoader();
		this.addXMLLoader("default", loader);
		loader = new ServicesXMLLoader();
		this.addXMLLoader("/services", loader);
		loader = new SessionsXMLLoader();
		this.addXMLLoader("/sessions", loader);
		loader = new ServersXMLLoader();
		this.addXMLLoader("/servers", loader);
		loader = new RespondersXMLLoader();
		this.addXMLLoader("/responders", loader);
		loader = new ResourceXMLLoader();
		this.addXMLLoader("/resource", loader);
		loader = new LogsXMLLoader();
		this.addXMLLoader("/logs", loader);
		loader = new AjaxResourceXMLLoader();
		this.addXMLLoader("/ajax/resource", loader);
		loader = new TimerXMLLoader();
		this.addXMLLoader("/ajax/timer", loader);
		loader = new AjaxSystemXMLLoader();
		this.addXMLLoader("/ajax/system", loader);
		loader = new AjaxContentXMLLoader();
		this.addXMLLoader("/ajax/content", loader);
	}

	/**
	 * Adds the xml loader.
	 * 
	 * @param key
	 *            the key
	 * @param loader
	 *            the loader
	 */
	public void addXMLLoader(String key, IXMLLoader loader) {
		this.loaderMap.put(key, loader);
	}

	/**
	 * Gets the xML loader.
	 * 
	 * @param url
	 *            the url
	 * @return the xML loader
	 */
	public IXMLLoader getXMLLoader(String url) {
		if (url == null) {
			return this.loaderMap.get("default");
		}
		int questionMarkPos = url.indexOf("?");
		if (questionMarkPos > 0) {
			url = url.substring(0, questionMarkPos);
		}
		IXMLLoader xmlLoader = this.loaderMap.get(url);
		if (xmlLoader == null) {
			xmlLoader = this.loaderMap.get("default");
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
			ServiceRegistry serviceRegistry = AppContext.getCurrentContext().getServiceRegistry();
			writer.writeStartElement("services");
			String serviceParameter = request.getParameter("service");
			Service[] services = serviceRegistry.getServices();
			for (Service service : services) {
				writer.writeStartElement("service");
				this.writeBean(writer, service);
				if (service instanceof PublishService) {
					PublishService publishService = (PublishService) service;
					SubscriptionQueue<SCMPMessage> subscriptionQueue = publishService.getSubscriptionQueue();
					writer.writeStartElement("subscriptionQueueSize");
					writer.writeCData(String.valueOf(subscriptionQueue.getSize()));
					writer.writeEndElement();
				}
				if (service.getServiceName().equals(serviceParameter)) {
					// take a look into
					writer.writeStartElement("details");
					
					// TODO DANI - JOT: Musste hier einen if einbauen.. bitte anpassen!
					if (service.getType() == ServiceType.SESSION_SERVICE
							|| service.getType() == ServiceType.PUBLISH_SERVICE) {

						List<SessionServer> serverList = ((AbstractSessionService) service).getServerList();
						writer.writeStartElement("servers");
						for (Server server : serverList) {
							writer.writeStartElement("server");
							this.writeBean(writer, server);
							writer.writeEndElement(); // close servers tag
						}
						writer.writeEndElement(); // close servers tag
					}
					if (service instanceof PublishService) {
						PublishService publishService = (PublishService) service;
						SubscriptionQueue<SCMPMessage> subscriptionQueue = publishService.getSubscriptionQueue();
						writer.writeStartElement("subscriptionQueue");
						Iterator<SCMPMessage> sqIter = subscriptionQueue.iterator();
						while (sqIter.hasNext()) {
							SCMPMessage scmpMessage = sqIter.next();
							writer.writeStartElement("scmpMessage");
							writer.writeStartElement("header");
							Map<String, String> header = scmpMessage.getHeader();
							for (Entry headerEntry : header.entrySet()) {
								writer.writeStartElement((String) headerEntry.getKey());
								writer.writeCData((String) headerEntry.getValue());
								writer.writeEndElement();
							}
							writer.writeEndElement();
							writer.writeEndElement();
						}
						writer.writeEndElement();
					}
					writer.writeEndElement(); // close details tag
				}
				writer.writeEndElement(); // close services tag
			}
			writer.writeEndElement(); // close services tag
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
			return new ServicesXMLLoader();
		}

	}

	/**
	 * The Class SessionsXMLLoader.
	 */
	public static class SessionsXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public SessionsXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			SessionRegistry sessionRegistry = AppContext.getCurrentContext().getSessionRegistry();
			writer.writeStartElement("sessions");
			String serviceParameter = request.getParameter("session");
			Session[] sessions = sessionRegistry.getSessions();
			for (Session session : sessions) {
				writer.writeStartElement("session");
				this.writeBean(writer, session);
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
			return new SessionsXMLLoader();
		}

	}

	/**
	 * The Class ServersXMLLoader.
	 */
	public static class ServersXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public ServersXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			ServerRegistry serverRegistry = AppContext.getCurrentContext().getServerRegistry();
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

	/**
	 * The Class RespondersXMLLoader.
	 */
	public static class RespondersXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public RespondersXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			ResponderRegistry responderRegistry = AppContext.getCurrentContext().getResponderRegistry();
			writer.writeStartElement("responders");
			IResponder[] responders = responderRegistry.getResponders();
			for (IResponder responder : responders) {
				writer.writeStartElement("responder");
				writer.writeStartElement("responderConfig");
				this.writeBean(writer, responder.getResponderConfig());
				writer.writeEndElement();
				writer.writeStartElement("endPoint");
				this.writeBean(writer, responder.getEndpoint());
				writer.writeEndElement();
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
			return new RespondersXMLLoader();
		}

	}

	/**
	 * The Class LogXMLLoader.
	 */
	public static class LogsXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public LogsXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			writer.writeStartElement("logs");
			String dateParameter = request.getParameter("date");
			Date today = new Date();
			today = new Date(today.getYear(), today.getMonth(), today.getDate());
			Date current = today;
			if (dateParameter != null) {
				current = WebUtil.getXMLDateFromString(dateParameter);
			}
			if (today.before(current)) {
				current = today;
			}
			// get previous and next date
			String next = WebUtil.getXMLNextDateAsString(current);
			String previous = WebUtil.getXMLPreviousDateAsString(current);
			// set selected date
			writer.writeAttribute("previous", previous);
			writer.writeAttribute("current", WebUtil.getXMLDateAsString(current));
			if (current.before(today)) {
				writer.writeAttribute("next", next);
			}
			Logger rootLogger = LogManager.getRootLogger();
			writeLogger(writer, rootLogger, today, current);
			Enumeration currentLoggers = LogManager.getCurrentLoggers();
			while (currentLoggers.hasMoreElements()) {
				Logger currentLogger = (Logger) currentLoggers.nextElement();
				writeLogger(writer, currentLogger, today, current);
			}
			writer.writeEndElement(); // close logs tag
		}

		private void writeLogger(XMLStreamWriter writer, Logger logger, Date today, Date current)
				throws XMLStreamException {
			writer.writeStartElement("logger");
			writer.writeAttribute("name", logger.getName());
			Enumeration appenders = logger.getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = (Appender) appenders.nextElement();
				writer.writeStartElement("appender");
				writer.writeAttribute("name", appender.getName());
				if (appender instanceof FileAppender) {
					writer.writeAttribute("type", "file");
					FileAppender fileAppender = (FileAppender) appender;
					String sFile = fileAppender.getFile();
					if (current.before(today)) {
						sFile += "." + WebUtil.getXMLDateAsString(current);
					}
					writer.writeStartElement("file");
					File file = new File(sFile);
					if (file.exists() && file.isFile()) {
						long length = file.length();
						writer.writeAttribute("size", String.valueOf(length));
					}
					writer.writeCData(sFile);
					writer.writeEndElement();
				}
				writer.writeEndElement(); // close appender tag
			}
			writer.writeEndElement(); // close logger tag
		}

		@Override
		public IFactoryable newInstance() {
			return new LogsXMLLoader();
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
			InputStream is = WebUtil.loadResource(name);
			if (is == null) {
				this.addMeta("exception", "resource for name = " + name + " not found");
				return;
			}
			try {
				writer.writeStartElement("resource");
				writer.writeAttribute("name", name);
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

	/**
	 * The Class AjaxSystemXMLLoader.
	 */
	public static class AjaxSystemXMLLoader extends AbstractXMLLoader {
		/**
		 * Instantiates a new system xml loader.
		 */
		public AjaxSystemXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public IFactoryable newInstance() {
			return new AjaxSystemXMLLoader();
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
			String action = request.getParameter("action");
			if ("gc".equals(action)) {
				System.gc();
				logger.info("run gc");
			}
		}
	}

	/**
	 * The Class AjaxContentXMLLoader.
	 */
	public static class AjaxContentXMLLoader extends AbstractXMLLoader {
		/**
		 * Instantiates a new system xml loader.
		 */
		public AjaxContentXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public IFactoryable newInstance() {
			return new AjaxContentXMLLoader();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isText() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String id = request.getParameter("id");
			if (id == null) {
				throw new InvalidParameterException("id parameter missing");
			}
			IXMLLoader loader = WebContext.getCurrentContext().getXMLLoader("/" + id);
			if (loader == null) {
				throw new NotFoundException();
			}
			loader.loadBody(writer, request);

		}
	}

}
