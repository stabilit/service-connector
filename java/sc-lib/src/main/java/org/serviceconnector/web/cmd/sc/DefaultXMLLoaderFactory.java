/*
 * Copyright � 2010 STABILIT Informatik AG, Switzerland *
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
package org.serviceconnector.web.cmd.sc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cache.Cache;
import org.serviceconnector.cache.CacheComposite;
import org.serviceconnector.cache.CacheException;
import org.serviceconnector.cache.CacheId;
import org.serviceconnector.cache.CacheKey;
import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.cache.CacheMessage;
import org.serviceconnector.cache.ICacheConfiguration;
import org.serviceconnector.conf.WebConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.net.res.IEndpoint;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.registry.ServerRegistry;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.registry.SessionRegistry;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Service;
import org.serviceconnector.service.Session;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.DumpUtility;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.NotFoundException;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.ctx.WebContext;

/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class DefaultXMLLoaderFactory {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(DefaultXMLLoaderFactory.class);

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
		loader = new SubscriptionsXMLLoader();
		this.addXMLLoader("/subscriptions", loader);
		loader = new ServersXMLLoader();
		this.addXMLLoader("/servers", loader);
		loader = new RespondersXMLLoader();
		this.addXMLLoader("/listeners", loader);
		loader = new ResourceXMLLoader();
		this.addXMLLoader("/resource", loader);
		loader = new LogsXMLLoader();
		this.addXMLLoader("/logs", loader);
		loader = new CacheXMLLoader();
		this.addXMLLoader("/cache", loader);
		loader = new MaintenanceXMLLoader();
		this.addXMLLoader("/maintenance", loader);
		loader = new DumpXMLLoader();
		this.addXMLLoader("/dump", loader);
		loader = new AjaxResourceXMLLoader();
		this.addXMLLoader("/ajax/resource", loader);
		loader = new TimerXMLLoader();
		this.addXMLLoader("/ajax/timer", loader);
		loader = new AjaxSystemXMLLoader();
		this.addXMLLoader("/ajax/system", loader);
		loader = new AjaxContentXMLLoader();
		this.addXMLLoader("/ajax/content", loader);
		loader = new AjaxMaintenanceXMLLoader();
		this.addXMLLoader("/ajax/maintenance", loader);
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
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			writer.writeStartElement("services");
			String serviceParameter = request.getParameter("service");
			String showSessionsParameter = request.getParameter("showsessions");
			Service[] services = serviceRegistry.getServices();
			for (Service service : services) {
				writer.writeStartElement("service");
				this.writeBean(writer, service);
				if (service instanceof PublishService) {
					PublishService publishService = (PublishService) service;
					PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
					writer.writeStartElement("publishMessageQueueSize");
					writer.writeCData(String.valueOf(publishMessageQueue.getSize()));
					writer.writeEndElement();  // end of publishMessageQueueSize
				}
				if (service.getName().equals(serviceParameter)) {
					// take a look into
					writer.writeStartElement("details");
					if (service instanceof StatefulService) {
						List<StatefulServer> serverList = ((StatefulService) service).getServerList();
						writer.writeStartElement("servers");
						for (StatefulServer server : serverList) {
							writer.writeStartElement("server");
							this.writeBean(writer, server);
							writer.writeEndElement(); // close server tag
						}
						writer.writeEndElement(); // close servers tag
					}
					if (service instanceof PublishService) {
 						PublishService publishService = (PublishService) service;
						PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
						writer.writeStartElement("publishMessageQueue");
						Iterator<SCMPMessage> sqIter = publishMessageQueue.iterator();
						while (sqIter.hasNext()) {
							SCMPMessage scmpMessage = sqIter.next();
							writer.writeStartElement("scmpMessage");
							writer.writeStartElement("header");
							Map<String, String> header = scmpMessage.getHeader();
							for (Entry<?, ?> headerEntry : header.entrySet()) {
								writer.writeStartElement((String) headerEntry.getKey());
								Object value = headerEntry.getValue();
								if (value != null) {
								   writer.writeCData(value.toString());
								}
								writer.writeEndElement();
							}
							writer.writeEndElement();  // end of header
							writer.writeEndElement();  // end of scmpMessage
						}
						writer.writeEndElement(); // end of publishMessageQueue
					}
					writer.writeEndElement(); // end details tag
				}
				writer.writeEndElement(); // end service tag
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
			String serverParameter = request.getParameter("server");
			if (serverParameter != null) {
				ServerRegistry serverRegistry = AppContext.getServerRegistry();
				Server server = serverRegistry.getServer(serverParameter);
				if (server != null) {
					writer.writeStartElement("server");
					this.writeBean(writer, server);
					writer.writeEndElement();
				}
			}
			String serviceParameter = request.getParameter("service");
			if (serviceParameter != null) {
				ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
				Service service = serviceRegistry.getService(serviceParameter);
				if (service != null) {
					writer.writeStartElement("service");
					this.writeBean(writer, service);
					writer.writeEndElement();
				}
			}			
			SessionRegistry sessionRegistry = AppContext.getSessionRegistry();
			writer.writeStartElement("sessions");
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
	 * The Class SubscriptionsXMLLoader.
	 */
	public static class SubscriptionsXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public SubscriptionsXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String serverParameter = request.getParameter("server");
			if (serverParameter != null) {
				ServerRegistry serverRegistry = AppContext.getServerRegistry();
				Server server = serverRegistry.getServer(serverParameter);
				if (server != null) {
					writer.writeStartElement("server");
					this.writeBean(writer, server);
					writer.writeEndElement();
				}
			}
			String serviceParameter = request.getParameter("service");
			if (serviceParameter != null) {
				ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
				Service service = serviceRegistry.getService(serviceParameter);
				if (service != null) {
					writer.writeStartElement("service");
					if (service instanceof PublishService) {
						PublishService publishService = (PublishService) service;
						PublishMessageQueue<SCMPMessage> publishMessageQueue = publishService.getMessageQueue();
						writer.writeStartElement("publishMessageQueueSize");
						writer.writeCData(String.valueOf(publishMessageQueue.getSize()));
						writer.writeEndElement();  // end of publishMessageQueueSize
					}
					this.writeBean(writer, service);
					writer.writeEndElement();
				}
			}
			SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
			writer.writeStartElement("subscriptions");
			Subscription[] subscriptions = subscriptionRegistry.getSubscriptions();
			for (Subscription subscription : subscriptions) {
				writer.writeStartElement("subscription");
				this.writeBean(writer, subscription);
				writer.writeEndElement();
			}
			writer.writeEndElement(); // close subscriptions tag
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
			return new SubscriptionsXMLLoader();
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
			ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
			writer.writeStartElement("responders");
			IResponder[] responders = responderRegistry.getResponders();
			for (IResponder responder : responders) {
				writer.writeStartElement("responder");
				writer.writeStartElement("responderConfig");
				this.writeBean(writer, responder.getListenerConfig());
				writer.writeEndElement();
				writer.writeStartElement("endPoints");
				List<IEndpoint> endPointList = responder.getEndpoints();
				for (IEndpoint endPoint : endPointList) {
					writer.writeStartElement("endPoint");
					this.writeBean(writer, endPoint);
					writer.writeEndElement();
				}
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

		/** The distinct logger set, prevent multiple entries of log files */
		private Set<String> distinctLoggerSet;

		/**
		 * Instantiates a new default xml loader.
		 */
		public LogsXMLLoader() {
			distinctLoggerSet = new HashSet<String>();
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			writer.writeStartElement("logs");
			String dateParameter = request.getParameter("date");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date today = cal.getTime();
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
			Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
			while (currentLoggers.hasMoreElements()) {
				Logger currentLogger = (Logger) currentLoggers.nextElement();
				Enumeration<?> appenders = currentLogger.getAllAppenders();
				if (appenders.hasMoreElements()) {
					writeLogger(writer, currentLogger, today, current);
				}
			}
			writer.writeEndElement(); // close logs tag
		}

		protected void writeLogger(XMLStreamWriter writer, Logger logger, Date today, Date current) throws XMLStreamException {
			writer.writeStartElement("logger");
			writer.writeAttribute("name", logger.getName());
			Enumeration<?> appenders = logger.getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = (Appender) appenders.nextElement();
				String appenderName = appender.getName();
				if (distinctLoggerSet.contains(appenderName)) {
					continue;
				}
				distinctLoggerSet.add(appenderName);
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
					if (sFile != null) {
					   writer.writeCData(sFile);
					}
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

		protected List<String> getCurrentLogFiles() {
			distinctLoggerSet.clear();
			List<String> logFileList = new ArrayList<String>();
			Logger rootLogger = LogManager.getRootLogger();
			addLogFiles(rootLogger, logFileList);
			Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
			while (currentLoggers.hasMoreElements()) {
				Logger currentLogger = (Logger) currentLoggers.nextElement();
				Enumeration<?> appenders = currentLogger.getAllAppenders();
				if (appenders.hasMoreElements()) {
					addLogFiles(currentLogger, logFileList);
				}
			}
			return logFileList;
		}

		protected void addLogFiles(Logger logger, List<String> logFileList) {
			Enumeration<?> appenders = logger.getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = (Appender) appenders.nextElement();
				String appenderName = appender.getName();
				if (distinctLoggerSet.contains(appenderName)) {
					continue;
				}
				distinctLoggerSet.add(appenderName);
				if (appender instanceof FileAppender) {
					FileAppender fileAppender = (FileAppender) appender;
					String sFile = fileAppender.getFile();
					File file = new File(sFile);
					if (file.exists() && file.isFile()) {
						logFileList.add(sFile);
					}
				}
			}
		}

	}

	/**
	 * The Class CacheXMLLoader.
	 */
	public static class CacheXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public CacheXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			writer.writeStartElement("cache");
			CacheManager cacheManager = AppContext.getCacheManager();
			ICacheConfiguration cacheConfiguration = cacheManager.getCacheConfiguration();
			this.writeCacheConfiguration(writer, cacheConfiguration);
			writer.writeEndElement(); // close cache tag
			writer.writeStartElement("caches");
			Object[] caches = cacheManager.getAllCaches();
			this.writeCaches(writer, caches, request);
			writer.writeEndElement(); // close caches tag
		}

		private void writeCacheConfiguration(XMLStreamWriter writer, ICacheConfiguration cacheConfiguration)
				throws XMLStreamException {
			writer.writeStartElement("config");
			writer.writeStartElement("diskPath");
			writer.writeCharacters(cacheConfiguration.getDiskPath());
			writer.writeEndElement(); // close diskPath tag
			writer.writeStartElement("maxElementsInMemory");
			writer.writeCharacters(String.valueOf(cacheConfiguration.getMaxElementsInMemory()));
			writer.writeEndElement(); // close maxElementsInMemory tag
			writer.writeStartElement("maxElementsOnDisk");
			writer.writeCharacters(String.valueOf(cacheConfiguration.getMaxElementsOnDisk()));
			writer.writeEndElement(); // close maxElementsOnDisk tag
			writer.writeStartElement("enabled");
			writer.writeCharacters(String.valueOf(cacheConfiguration.isCacheEnabled()));
			writer.writeEndElement(); // close enabled tag
			writer.writeEndElement(); // close config tag
		}

		private void writeCaches(XMLStreamWriter writer, Object[] caches, IWebRequest request) throws XMLStreamException {
			String cacheParam = request.getParameter("cache");
			for (Object obj : caches) {
				if (obj instanceof Cache == false) {
					continue;
				}
				Cache cache = (Cache) obj;
				writer.writeStartElement("cache");
				writer.writeStartElement("serviceName");
				writer.writeCharacters(cache.getServiceName());
				writer.writeEndElement(); // close serviceName tag
				writer.writeStartElement("compositeSize");
				writer.writeCharacters(String.valueOf(cache.getCompositeSize()));
				writer.writeEndElement(); // close compositeSize tag
				writer.writeStartElement("elementSize");
				writer.writeCharacters(String.valueOf(cache.getElementSize()));
				writer.writeEndElement(); // close elementSize tag
				writer.writeStartElement("memoryStoreSize");
				writer.writeCharacters(String.valueOf(cache.getMemoryStoreSize()));
				writer.writeEndElement(); // close memoryStoreSize tag
				writer.writeStartElement("diskStoreSize");
				writer.writeCharacters(String.valueOf(cache.getDiskStoreSize()));
				writer.writeEndElement(); // close diskStoreSize tag
				if (cache.getServiceName().equals(cacheParam)) {
					writeCacheDetails(writer, cache, request);
				}
				writer.writeEndElement(); // close cache tag
			}
		}

		private void writeCacheDetails(XMLStreamWriter writer, Cache cache, IWebRequest request) throws XMLStreamException {
			writer.writeStartElement("details");
			Object[] compositeKeys = cache.getCompositeKeys();
			if (compositeKeys == null) {
				writer.writeEndElement();
				return;
			}
			for (Object obj : compositeKeys) {
				CacheKey cacheKey = (CacheKey) obj;
				try {
					CacheComposite cacheComposite = cache.getComposite(cacheKey.getCacheId());
					if (cacheComposite != null) {
						writeCacheComposite(writer, cache, cacheKey, cacheComposite, request);
					}
				} catch (CacheException e) {
				}
			}
			writer.writeEndElement(); // end of details
		}

		private void writeCacheComposite(XMLStreamWriter writer, Cache cache, CacheKey cacheKey, CacheComposite cacheComposite,
				IWebRequest request) throws XMLStreamException {
			String compositeParam = request.getParameter("composite");
			writer.writeStartElement("composite");
			writer.writeStartElement("key");
			writer.writeCharacters(cacheKey.getCacheId());
			writer.writeEndElement();
			writer.writeStartElement("state");
			writer.writeCharacters(cacheComposite.getCacheState().toString());
			writer.writeEndElement(); // end of state
			writer.writeStartElement("size");
			writer.writeCharacters(String.valueOf(cacheComposite.getSize()));
			writer.writeEndElement(); // end of size
			writer.writeStartElement("loadingTimeout");
			writer.writeCharacters(String.valueOf(cacheComposite.getLoadingTimeout()));
			writer.writeEndElement(); // end of loadingTimeout
			writer.writeStartElement("expiration");
			if (cacheComposite.getExpiration() != null) {
				writer.writeCharacters(cacheComposite.getExpiration());
			}
			writer.writeEndElement(); // end of expiration
			writer.writeStartElement("creation");
			writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cacheComposite.getCreationTime()));
			writer.writeEndElement(); // end of creation
			writer.writeStartElement("lastModified");
			writer.writeCharacters(DateTimeUtility.getDateTimeAsString(cacheComposite.getLastModifiedTime()));
			writer.writeEndElement(); // end of lastModified
			if (compositeParam != null && compositeParam.equals(cacheKey.getCacheId())) {
				// get all messages
				CacheId cacheId = new CacheId(cacheKey.getCacheId());
				for (int i = 0; i < cacheComposite.getSize(); i++) {
					cacheId.setSequenceNr(String.valueOf(i + 1));
					writer.writeStartElement("message");
					try {
						CacheMessage cacheMessage = cache.getMessage(cacheId.getFullCacheId());
						writeCacheMessage(writer, cacheMessage);
					} catch (CacheException e) {
					}
					writer.writeEndElement();

				}
			}
			writer.writeEndElement(); // end of composite
		}

		/**
		 * Write cache message.
		 * 
		 * @param cacheMessage
		 *            the cache message
		 * @throws XMLStreamException
		 */
		private void writeCacheMessage(XMLStreamWriter writer, CacheMessage cacheMessage) throws XMLStreamException {
			if (cacheMessage == null) {
				return;
			}
			writer.writeStartElement("id");
			writer.writeCharacters(cacheMessage.getCacheId().getFullCacheId());
			writer.writeEndElement();
			writer.writeStartElement("sequenceNr");
			writer.writeCharacters(cacheMessage.getCacheId().getSequenceNr());
			writer.writeEndElement();
			writer.writeStartElement("messageType");
			writer.writeCharacters(cacheMessage.getMessageType());
			writer.writeEndElement();
			writer.writeStartElement("compressed");
			writer.writeCharacters(String.valueOf(cacheMessage.isCompressed()));
			writer.writeEndElement();
			Object body = cacheMessage.getBody();
			if (body != null && body instanceof byte[]) {
				writer.writeStartElement("bodyLength");
				writer.writeCharacters(String.valueOf(((byte[]) body).length));
				writer.writeEndElement();
			}
			if (body != null && body instanceof String) {
				writer.writeStartElement("bodyLength");
				writer.writeCharacters(String.valueOf(((String) body).length()));
				writer.writeEndElement();
			}
		}

		@Override
		public IFactoryable newInstance() {
			return new CacheXMLLoader();
		}
	}

	/**
	 * The Class DumpXMLLoader.
	 */
	public static class DumpXMLLoader extends AbstractXMLLoader {

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

	/**
	 * The Class MaintenanceXMLLoader.
	 */
	public static class MaintenanceXMLLoader extends AbstractXMLLoader {

		/**
		 * Instantiates a new default xml loader.
		 */
		public MaintenanceXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public final void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			writer.writeStartElement("maintenance");
			// load web configuration
			loadWebConfiguration(writer, request);
			// load any file services
			loadFileServices(writer, request);

			writer.writeEndElement(); // close maintenance tag
		}

		private void loadWebConfiguration(XMLStreamWriter writer, IWebRequest request) throws Exception {
			WebConfiguration webConfiguration = WebContext.getWebConfiguration();
			writer.writeStartElement("web-config");
			String scDownloadService = webConfiguration.getScDownloadService();
			writer.writeStartElement("scDownloadService");
			if (scDownloadService != null) {
			   writer.writeCData(scDownloadService);
			}
			writer.writeEndElement(); // end of scDownloadService
			String scUploadService = webConfiguration.getScUploadService();
			writer.writeStartElement("scUploadService");
			if (scUploadService != null) {
			   writer.writeCData(scUploadService);
			}
			writer.writeEndElement(); // end of scUploadService
			writer.writeEndElement(); // end of web-config
		}

		private void loadFileServices(XMLStreamWriter writer, IWebRequest request) throws Exception {
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			writer.writeStartElement("services");
			Service[] services = serviceRegistry.getServices();
			WebConfiguration webConfiguration = WebContext.getWebConfiguration();
			String scDownloadService = webConfiguration.getScDownloadService();
			String scUploadService = webConfiguration.getScUploadService();
			for (Service service : services) {
				if (service instanceof FileService) {
					// check if upload or download is active for this service
					FileService fileService = (FileService) service;
					writer.writeStartElement("service");
					String fileServiceName = fileService.getName();
					if (fileServiceName.equals(scDownloadService)) {
						writer.writeStartElement("scDownloadService");
						writer.writeCharacters("true");
						writer.writeEndElement();
					}
					if (fileServiceName.equals(scUploadService)) {
						writer.writeStartElement("scUploadService");
						writer.writeCharacters("true");
						writer.writeEndElement();
					}
					this.writeBean(writer, service);
					writer.writeEndElement(); // close service tag
				}
			}
			writer.writeEndElement(); // close services tag
		}

		@Override
		public IFactoryable newInstance() {
			return new MaintenanceXMLLoader();
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
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String action = request.getParameter("action");
			writer.writeStartElement("system");
			writer.writeStartElement("action");
			if (action != null) {
			   writer.writeCData(action);
			}
			writer.writeEndElement(); // action
			try {
				if ("gc".equals(action)) {
					logger.debug("run gc");
					System.gc();
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					writer.writeStartElement("messages");
					writer.writeStartElement("message");
					writer.writeCharacters("GC did run.");
					writer.writeEndElement(); // message
					writer.writeEndElement(); // messages
					return;
				}
				if ("dump".equals(action)) {
					logger.debug("SC dump by user interface");
					try {
						String dumpPath = AppContext.dump();
						writer.writeStartElement("status");
						writer.writeCharacters("success");
						writer.writeEndElement();
						writer.writeStartElement("messages");
						writer.writeStartElement("message");
						writer.writeCharacters("SC dump done.");
						writer.writeEndElement(); // message
						writer.writeStartElement("message");
						writer.writeCharacters(dumpPath);
						writer.writeEndElement(); // message
						writer.writeEndElement(); // messages
					} catch (Exception e) {
						writer.writeStartElement("status");
						writer.writeCharacters("failure");
						writer.writeEndElement();
						writer.writeStartElement("messages");
						writer.writeStartElement("message");
						writer.writeCharacters(e.toString());
						writer.writeEndElement(); // message
						writer.writeEndElement(); // messages
					}
					return;
				}
				if ("terminate".equals(action)) {
					logger.debug("SC terminated by user interface");
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					writer.writeStartElement("messages");
					writer.writeStartElement("message");
					writer.writeCharacters("SC has been terminated.");
					writer.writeEndElement(); // message
					writer.writeEndElement(); // messages
					System.exit(1);
				}
				if ("deleteDump".equals(action)) {
					logger.debug("delete dump by user interface");
					String dumpPath = AppContext.getBasicConfiguration().getDumpPath();
					DumpUtility.deleteAllDumpFiles(dumpPath);
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					writer.writeStartElement("messages");
					writer.writeStartElement("message");
					writer.writeCharacters("Dump files have been deleted.");
					writer.writeEndElement(); // message
					writer.writeEndElement(); // messages
					return;
				}
				if ("clearCache".equals(action)) {
					logger.debug("clear cache by user interface");
					CacheManager cacheManager = AppContext.getCacheManager();
					cacheManager.clearAll();
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					writer.writeStartElement("messages");
					writer.writeStartElement("message");
					writer.writeCharacters("Cache has been cleared.");
					writer.writeEndElement(); // message
					writer.writeEndElement(); // messages
					return;
				}
				if ("resetTranslet".equals(action)) {
					logger.debug("reset translet by user interface");
					XSLTTransformerFactory.getInstance().clearTranslet();
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					writer.writeStartElement("messages");
					writer.writeStartElement("message");
					writer.writeCharacters("Translet have been reset.");
					writer.writeEndElement(); // message
					writer.writeEndElement(); // messages
					return;
				}
				if ("downloadAndReplace".equals(action)) {
					logger.debug("download and replace configuration");
					downloadAndReplace(writer, request);
					writer.writeStartElement("status");
					writer.writeCharacters("success");
					writer.writeEndElement();
					return;
				}
				if ("uploadLogFiles".equals(action)) {
					logger.debug("upload current log files");
					uploadCurrentLogFiles(writer, request);
					return;
				}
				// action is not valid or unknown
				writer.writeStartElement("status");
				writer.writeCharacters("failure");
				writer.writeEndElement();
				writer.writeStartElement("messages");
				writer.writeStartElement("message");
				writer.writeCharacters("Action [" + action + "] is unknwon");
				writer.writeEndElement(); // message
				writer.writeEndElement(); // messages
			} catch (Exception e) {
				writer.writeStartElement("status");
				writer.writeCharacters("failure");
				writer.writeEndElement();
				writer.writeStartElement("messages");
				writer.writeStartElement("message");
				writer.writeCharacters(e.getMessage());
				writer.writeEndElement(); // message
				writer.writeEndElement(); // messages
			} finally {
				writer.writeEndElement();
			}
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(Writer writer, IWebRequest request) throws Exception {
			throw new UnsupportedOperationException();
		}

		/**
		 * Download and replace all selected from remote file server into our current configuration directory.
		 * If the same file already exists then the local file content will be replaced.
		 * 
		 * @param writer
		 *            the writer
		 * @param request
		 *            the request
		 * @throws Exception
		 *             the exception
		 */
		private void downloadAndReplace(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String serviceName = request.getParameter("service");
			if (serviceName == null) {
				throw new WebCommandException("service is missing");
			}
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			Service service = serviceRegistry.getService(serviceName);
			if (service == null) {
				throw new WebCommandException("service " + serviceName + " not found");
			}
			if (service instanceof FileService == false) {
				throw new WebCommandException("service " + serviceName + " is not a file service");
			}
			writer.writeStartElement("service");
			writer.writeCharacters(serviceName);
			writer.writeEndElement();
			FileService fileService = (FileService) service;
			FileServer fileServer = fileService.getServer();
			List<String> fileList = request.getParameterList("file");
			writer.writeStartElement("messages");
			writer.writeStartElement("message");
			writer.writeCharacters("The following files were downloaded from file service [" + serviceName + "]:");
			writer.writeEndElement();
			if (fileList != null) {
				for (String file : fileList) {
					if (file.startsWith("fs:") && file.endsWith(":fs")) {
						try {
							file = file.substring(3, file.length() - 3);
							String configFileName = SystemInfo.getConfigFileName();
							File configFile = new File(configFileName);
							File localDestinationFile = null;
							if (configFile.isAbsolute()) {
								localDestinationFile = new File(configFile.getParent() + File.separator + file);
							} else {
								URL resourceURL = WebUtil.getResourceURL(configFileName);
								File resourceURLFile = new File(resourceURL.toURI());
								localDestinationFile = new File(resourceURLFile.getParent() + File.separator + file);
							}
							downloadAndReplaceSingleFile(writer, fileServer, fileService, file, localDestinationFile);
						} catch (Exception e) {
							writer.writeStartElement("message");
							writer.writeCharacters(file + " did fail, " + e.getMessage());
							writer.writeEndElement();
						}
					} else {
						writer.writeStartElement("message");
						writer.writeCharacters(file + "  invalid format");
						writer.writeEndElement();
					}
				}
			}
			writer.writeEndElement();
		}

		/**
		 * Download and replace a single file.
		 * 
		 * @param writer
		 *            the writer
		 * @param srcUrl
		 *            the src url
		 * @param dstFile
		 *            the dst file
		 * @throws Exception
		 *             the exception
		 */
		private void downloadAndReplaceSingleFile(XMLStreamWriter writer, FileServer fileServer, FileService fileService,
				String remoteFile, File dstFile) throws Exception {
			String status = "successful (copied)";
			if (dstFile.exists()) {
				status = "successful (replaced)";
			}
			try {
				fileServer.downloadAndReplace(fileService, remoteFile, dstFile);
				writer.writeStartElement("message");
				writer.writeCharacters(dstFile.getName() + "  " + status);
				writer.writeEndElement();
			} catch (Exception e) {
				status = "failed";
				writer.writeStartElement("message");
				writer.writeCharacters(dstFile.getName() + "  " + status);
				writer.writeEndElement();
				throw e;
			}
		}

		/**
		 * Upload current log files to remote file server. The file server will be identified by the service name.
		 * 
		 * @param writer
		 *            the writer
		 * @param request
		 *            the request
		 * @throws Exception
		 *             the exception
		 */
		private void uploadCurrentLogFiles(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String serviceName = request.getParameter("service");
			if (serviceName == null) {
				throw new WebCommandException("service is missing");
			}
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			Service service = serviceRegistry.getService(serviceName);
			if (service == null) {
				throw new WebCommandException("service " + serviceName + " not found");
			}
			if (service instanceof FileService == false) {
				throw new WebCommandException("service " + serviceName + " is not a file service");
			}
			writer.writeStartElement("service");
			writer.writeCharacters(serviceName);
			writer.writeEndElement();
			// get current log files and write them to the remote file server using a stream
			FileService fileService = (FileService) service;
			FileServer fileServer = fileService.getServer();
			try {
				String logsFileName = fileServer.uploadCurrentLogFiles(fileService, serviceName);
				writer.writeStartElement("status");
				writer.writeCharacters("success");
				writer.writeEndElement();
				writer.writeStartElement("messages");
				writer.writeStartElement("message");
				writer.writeCharacters("logs file upload done!");
				writer.writeEndElement();
				writer.writeStartElement("message");
				writer.writeCharacters("file name is " + logsFileName);
				writer.writeEndElement();
				writer.writeEndElement();
			} catch (Exception e) {
				writer.writeStartElement("status");
				writer.writeCharacters("failure");
				writer.writeEndElement();
				writer.writeStartElement("messages");
				writer.writeStartElement("message");
				writer.writeCharacters("logs file upload did fail");
				writer.writeEndElement();
				writer.writeStartElement("message");
				writer.writeCharacters(e.toString());
				writer.writeEndElement();
				writer.writeEndElement();
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
			IXMLLoader loader = WebContext.getXMLLoader("/" + id);
			if (loader == null) {
				throw new NotFoundException();
			}
			loader.loadBody(writer, request);

		}
	}

	/**
	 * The Class AjaxMaintenanceXMLLoader.
	 */
	public static class AjaxMaintenanceXMLLoader extends AbstractXMLLoader {
		/**
		 * Instantiates a new system xml loader.
		 */
		public AjaxMaintenanceXMLLoader() {
		}

		/** {@inheritDoc} */
		@Override
		public IFactoryable newInstance() {
			return new AjaxMaintenanceXMLLoader();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isText() {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String action = request.getParameter("action");
			if (action == null) {
				throw new InvalidParameterException("action parameter missing");
			}
			if ("sc_property_download".equals(action)) {
				loadPropertyDownloadBody(writer, request);
				return;
			}
			if ("sc_logs_upload".equals(action)) {
				loadLogfileUploadBody(writer, request);
				return;
			}
			if ("sc_dump_list".equals(action)) {
				loadDumpListBody(writer, request);
				return;
			}			
			throw new InvalidParameterException("action parameter is invalid or unknown (action=" + action + ")");
		}

		/**
		 * load body data for property files download action
		 * 
		 * @param writer
		 * @param request
		 * @throws Exception
		 */
		private void loadPropertyDownloadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String serviceName = request.getParameter("service");
			if (serviceName == null) {
				throw new InvalidParameterException("service parameter missing");
			}
			// load file services and the file list
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			writer.writeStartElement("service");
			Service service = serviceRegistry.getService(serviceName);
			this.writeBean(writer, service);
			if (service instanceof FileService) {
				FileService fileService = (FileService) service;
				FileServer fileServer = fileService.getServer();
				try {
					SCMPMessage reply = fileServer.serverGetFileList(fileService.getPath(), fileService.getGetFileListScriptName(),
							serviceName, 10);
					Object body = reply.getBody();
					if (body != null && body instanceof byte[]) {
						String sBody = new String((byte[]) body);
						String[] files = sBody.split("\\|");
						writer.writeStartElement("files");
						for (int i = 0; i < files.length; i++) {
							writer.writeStartElement("file");
							String file = files[i];
							if (file != null) {
							    writer.writeCData(files[i]);
							}
							writer.writeEndElement();
						}
						writer.writeEndElement();
					}
				} catch (Exception e) {
					writer.writeComment("exception:" + e.toString() + ":exception");
					writer.writeStartElement("exception");
					writer.writeCData(e.toString());
					writer.writeEndElement();
					logger.error(e.toString());
				}
			}
			writer.writeEndElement(); // close service tag
			// load current configuration directory
			String configFileName = SystemInfo.getConfigFileName();
			URL resourceURL = WebUtil.getResourceURL(configFileName);
			if (resourceURL != null) {
				writer.writeStartElement("resource");
				writer.writeStartElement("url");
				writer.writeCData(resourceURL.toString());
				writer.writeEndElement(); // close url tag
				File file = new File(resourceURL.getFile());
				String parent = file.getParent();
				if (parent != null) {
					File parentFile = new File(parent);
					File[] files = parentFile.listFiles();
					if (files != null) {
						writer.writeStartElement("files");
						for (int i = 0; i < files.length; i++) {
							if (files[i].isFile()) {
								writer.writeStartElement("file");
								String name = files[i].getName();
								if (name != null) {									
								   writer.writeCData(name);
								}
								writer.writeEndElement(); // close file tag
							}
						}
						writer.writeEndElement(); // close files tag
					}
				}
				writer.writeEndElement(); // close resource tag
			}
		}

		/**
		 * load body data for logs file upload action
		 * 
		 * @param writer
		 * @param request
		 * @throws Exception
		 */
		private void loadLogfileUploadBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String serviceName = request.getParameter("service");
			if (serviceName == null) {
				throw new InvalidParameterException("service parameter missing");
			}
			// load file services and the file list
			ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
			writer.writeStartElement("service");
			Service service = serviceRegistry.getService(serviceName);
			this.writeBean(writer, service);
			if (service instanceof FileService) {
				FileService fileService = (FileService) service;
				FileServer fileServer = fileService.getServer();
				try {
					SCMPMessage reply = fileServer.serverGetFileList(fileService.getPath(), fileService.getGetFileListScriptName(),
							serviceName, 10);
					Object body = reply.getBody();
					if (body != null && body instanceof byte[]) {
						String sBody = new String((byte[]) body);
						String[] files = sBody.split("\\|");
						writer.writeStartElement("files");
						for (int i = 0; i < files.length; i++) {
							String fileName = files[i];
							if (fileName.startsWith(Constants.LOGS_FILE_NAME)) {
								writer.writeStartElement("file");								
								writer.writeCData(fileName);
								writer.writeEndElement();
							}
						}
						writer.writeEndElement();
					}
				} catch (Exception e) {
					writer.writeComment("exception:" + e.toString() + ":exception");
					writer.writeStartElement("exception");
					writer.writeCData(e.toString());
					writer.writeEndElement();
					logger.error(e.toString());
				}
			}
			writer.writeEndElement(); // close service tag
			// get logs xml loader from factory
			LogsXMLLoader logsXMLLoader = (LogsXMLLoader) loaderFactory.getXMLLoader("/logs");
			// load available logs file list for current date (today)
			writer.writeStartElement("logs");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date today = cal.getTime();
			Logger rootLogger = LogManager.getRootLogger();
			logsXMLLoader.writeLogger(writer, rootLogger, today, today);
			Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
			while (currentLoggers.hasMoreElements()) {
				Logger currentLogger = (Logger) currentLoggers.nextElement();
				Enumeration<?> appenders = currentLogger.getAllAppenders();
				if (appenders.hasMoreElements()) {
					logsXMLLoader.writeLogger(writer, currentLogger, today, today);
				}
			}
			writer.writeEndElement(); // close logs tag
		}

		/**
		 * load body data for dump list action
		 * 
		 * @param writer
		 * @param request
		 * @throws Exception
		 */
		private void loadDumpListBody(XMLStreamWriter writer, IWebRequest request) throws Exception {
			String dumpPath = AppContext.getBasicConfiguration().getDumpPath();
			File[] files = DumpUtility.getDumpFiles(dumpPath);
			writer.writeStartElement("dumplist");
			writer.writeStartElement("path");
			if (dumpPath != null) {
			   writer.writeCData(dumpPath);
			}
			writer.writeEndElement(); // close path tag
			if (files != null) {
				writer.writeStartElement("files");
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						writer.writeStartElement("file");
						writer.writeStartElement("name");
						String name = files[i].getName();
						if (name != null) {
						   writer.writeCData(name);
						}
						writer.writeEndElement(); // close name tag
						writer.writeStartElement("length");
						writer.writeCData(String.valueOf(files[i].length()));
						writer.writeEndElement(); // close length tag
						writer.writeStartElement("lastModified");
						Date lastModifiedDate = new Date(files[i].lastModified());
						writer.writeCData(DateTimeUtility.getDateTimeAsString(lastModifiedDate));
						writer.writeEndElement(); // close last modified tag
						writer.writeEndElement(); // close file tag
					}
				}
				writer.writeEndElement(); // close files tag
			}
			writer.writeEndElement(); // close dumplist tag

			return;
		}

	}

}
