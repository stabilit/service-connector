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
package org.serviceconnector.web;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.serviceconnector.SCVersion;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.net.connection.ConnectionContext;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.connection.IConnection;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.ctx.WebContext;

/**
 * The Class AbstractXMLLoader.
 */
public abstract class AbstractXMLLoader implements IXMLLoader {

	/** The Constant XMLSDF. */
	public static final SimpleDateFormat XMLSDF = new SimpleDateFormat("yyyy-MM-dd");

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(AbstractXMLLoader.class);

	/** The meta map. */
	private Map<String, String> metaMap;

	/** The meta map list. */
	private List<Map<String, String>> metaMapList;

	/**
	 * Instantiates a new abstract xml loader.
	 */
	public AbstractXMLLoader() {
		this.metaMap = new HashMap<String, String>();
		this.metaMapList = new ArrayList<Map<String, String>>();
	}

	/**
	 * Checks if is text.
	 * 
	 * @return true, if is text {@inheritDoc}
	 */
	@Override
	public boolean isText() {
		return false;
	}

	/**
	 * Adds the meta.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value {@inheritDoc}
	 */
	@Override
	public void addMeta(String name, String value) {
		this.metaMap.put(name, value);
	}

	/**
	 * Adds the meta.
	 * 
	 * @param map
	 *            the map {@inheritDoc}
	 */
	@Override
	public void addMeta(Map<String, String> map) {
		this.metaMapList.add(map);
	}

	/**
	 * Load body.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	public abstract void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception;

	/**
	 * Load body.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	public void loadBody(Writer writer, IWebRequest request) throws Exception {

	}

	/**
	 * Load.
	 * 
	 * @param request
	 *            the request
	 * @param os
	 *            the os
	 * @throws Exception
	 *             the exception {@inheritDoc}
	 */
	@Override
	public final void load(IWebRequest request, OutputStream os) throws Exception {
		if (this.isText()) {
			OutputStreamWriter writer = new OutputStreamWriter(os);
			this.loadBody(writer, request);
			writer.flush();
			return;

		}
		IWebSession webSession = request.getSession(false);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(os);
		writer.writeStartDocument();
		writer.writeStartElement("sc-web");
		writer.writeStartElement("head");
		writer.writeStartElement("meta");
		writer.writeAttribute("creation", DateTimeUtility.getCurrentTimeZoneMillis());
		// write sc version
		writer.writeEndElement(); // close meta tag
		writer.writeStartElement("meta");
		writer.writeAttribute("scversion", SCVersion.CURRENT.toString());
		writer.writeEndElement(); // close meta tag
		// write sc header prefix
		writer.writeStartElement("meta");
		writer.writeAttribute("headerprefix", WebContext.getWebConfiguration().getPageHeaderPrefix());
		writer.writeEndElement(); // close meta tag
		// write sc status
		writer.writeStartElement("meta");
		writer.writeAttribute("scstatus", "success");
		writer.writeEndElement(); // close meta tag
		// write scconfigfile
		writer.writeStartElement("meta");
		writer.writeAttribute("scconfigfile", SystemInfo.getConfigFileName());
		writer.writeEndElement(); // close meta tag
		// write current ip host
		String hostName = InetAddress.getLocalHost().getHostName();
		writer.writeStartElement("meta");
		writer.writeAttribute("hostname", hostName);
		writer.writeEndElement(); // close meta tag
		if (webSession != null) {
			writer.writeStartElement("meta");
			writer.writeAttribute("jsessionid", webSession.getSessionId());
			writer.writeEndElement(); // close meta tag
		}
		for (Entry<String, String> entry : this.metaMap.entrySet()) {
			writer.writeStartElement("meta");
			writer.writeAttribute(entry.getKey(), entry.getValue());
			writer.writeEndElement(); // close meta tag
		}
		for (Map<String, String> map : this.metaMapList) {
			writer.writeStartElement("meta");
			for (Entry<String, String> entry : map.entrySet()) {
				writer.writeAttribute(entry.getKey(), entry.getValue());
			}
			writer.writeEndElement(); // close meta tag
		}
		// write any query params back
		writer.writeStartElement("query");
		Map<String, List<String>> parameterMap = request.getParameterMap();
		for (Entry<String, List<String>> parameter : parameterMap.entrySet()) {
			String name = parameter.getKey();
			if ("password".equals(name)) {
				continue;
			}
			List<String> values = parameter.getValue();
			for (String value : values) {
				writer.writeStartElement("param");
				writer.writeAttribute(name, value);
				writer.writeEndElement(); // close param
			}
		}
		writer.writeEndElement(); // close query
		writer.writeEndElement(); // close head
		writer.writeStartElement("body");
		this.writeSystem(writer);
		this.loadBody(writer, request);
		writer.writeEndElement(); // close body tag
		writer.writeEndElement(); // close root tag sc-web
		writer.writeEndDocument();
		writer.close();
	}

	/**
	 * Write system.
	 * 
	 * @param writer
	 *            the writer
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	public void writeSystem(XMLStreamWriter writer) throws XMLStreamException {
		// write system info
		SystemInfo systemInfo = new SystemInfo();
		writer.writeStartElement("system");
		writer.writeStartElement("info");
		this.writeBean(writer, systemInfo);
		writer.writeEndElement(); // close info tag
		// write runtime info
		writer.writeStartElement("runtime");
		this.writeRuntime(writer);
		writer.writeEndElement(); // end of runtime
		// write statistics info
		writer.writeStartElement("statistics");
		this.writeBean(writer, Statistics.getInstance());
		writer.writeEndElement(); // end of statistics
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

	/**
	 * Write bean.
	 * 
	 * @param writer
	 *            the writer
	 * @param obj
	 *            the obj
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	public void writeBean(XMLStreamWriter writer, Object obj) throws XMLStreamException {
		if (obj == null) {
			return;
		}
		// Field[] fields = obj.getClass().getDeclaredFields();
		// for (Field field : fields) {
		// String name = field.getName();
		// try {
		// writer.writeStartElement(name);
		// try {
		// Object value = BeanUtils.getProperty(obj, name);
		// if (value != null) {
		// writer.writeCData(value.toString());
		// }
		// } catch (Exception e) {
		// // we ignore this exception
		// }
		// writer.writeEndElement();
		// } catch (Exception e) {
		// }
		// }
		Method[] methods = obj.getClass().getMethods();
		Set<String> methodSet = new HashSet<String>();
		for (Method method : methods) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (method.getParameterTypes() == null) {
				continue;
			}
			if (parameterTypes.length > 0) {
				continue;
			}
			String name = method.getName();
			if (methodSet.contains(name)) {
				continue;
			}
			methodSet.add(name);
			if (name.startsWith("get") == false && name.startsWith("is") == false) {
				continue;
			}
			if (name.startsWith("get")) {
			   name = String.valueOf(name.charAt(3)).toLowerCase() + name.substring(4);
			} else {
			   name = String.valueOf(name.charAt(2)).toLowerCase() + name.substring(3);				
			}
			if ("class".equals(name)) {
				continue;
			}
			if ("context".equals(name)) {
				if (obj instanceof IConnection) {
					continue;
				}
			}
			if ("connection".equals(name)) {
				if (obj instanceof ConnectionPool) {
					continue;
				}
			}
			
			try {
				Object value = null;
				try {
					value = method.invoke(obj);
				} catch (Exception e) {
					System.err.println(e);
				}
				if (value == obj) {
					continue;
				}
				if (value != null) {
					if (value instanceof List<?>) {
						writer.writeStartElement(name);
						List<?> list = (List<?>) value;
						for (Object listObj : list) {
							writer.writeStartElement(listObj.getClass().getSimpleName().toLowerCase());
							if (listObj instanceof String) {
								writer.writeCData(listObj.toString());
							} else {
								this.writeBean(writer, listObj);
							}
							writer.writeEndElement();
						}
						writer.writeEndElement();
						continue;
					}
					if (value instanceof Server) {
						writer.writeStartElement(name);
						Server server = (Server) value;
						writer.writeStartElement("serverKey");
						writer.writeCData(server.getServerKey());
						writer.writeEndElement();
						writer.writeStartElement("serviceName");
						if (value instanceof StatefulServer) {
							writer.writeCData(((StatefulServer) server).getServiceName());
						} else {
							writer.writeCData("unknown");
						}
						writer.writeEndElement();
						writer.writeStartElement("host");
						writer.writeCData(server.getHost());
						writer.writeEndElement();
						writer.writeStartElement("port");
						writer.writeCData(String.valueOf(server.getPortNr()));
						writer.writeEndElement();
						writer.writeStartElement("socketAddress");
						writer.writeCData(String.valueOf(server.getSocketAddress()));
						writer.writeEndElement();
						writer.writeEndElement();
						continue;
					}
					if (value instanceof IRequester) {
						writer.writeStartElement(name);
						IRequester requester = (IRequester) value;
						writer.writeStartElement("context");
						this.writeBean(writer, requester.getRemoteNodeConfiguration());
						writer.writeEndElement();
						writer.writeEndElement();
						continue;
					}
					if (value instanceof ConnectionPool) {
						ConnectionPool connectionPool = (ConnectionPool) value;
						writer.writeStartElement("connectionPool");
						this.writeBean(writer, connectionPool);
						writer.writeEndElement();
						continue;
					}
					if (value instanceof ConnectionContext) {
						ConnectionContext connectionContext = (ConnectionContext) value;
						writer.writeStartElement("connectionContext");
						this.writeBean(writer, connectionContext);
						writer.writeEndElement();
						continue;
					}
					if (value instanceof IConnection) {
						IConnection connection = (IConnection) value;
						writer.writeStartElement("connection");
						this.writeBean(writer, connection);
						writer.writeEndElement();
						continue;
					}
					if (value instanceof ListenerConfiguration) {
						ListenerConfiguration listenerConfig = (ListenerConfiguration) value;
						writer.writeStartElement(name);
						this.writeBean(writer, listenerConfig);
						writer.writeEndElement();
						continue;
					}
					if (value instanceof RemoteNodeConfiguration) {
						RemoteNodeConfiguration remoteNodeConfiguration = (RemoteNodeConfiguration) value;
						writer.writeStartElement(name);
						this.writeBean(writer, remoteNodeConfiguration);
						writer.writeEndElement();
						continue;
					}
					if (value instanceof SubscriptionMask) {
						writer.writeStartElement("subscriptionMask");
						SubscriptionMask subscriptionMask = (SubscriptionMask) value;
						writer.writeCData(subscriptionMask.getValue());
						writer.writeEndElement();
					}
					writer.writeStartElement(name);
					writer.writeCData(value.toString());
					writer.writeEndElement();
				} else {
					writer.writeStartElement(name);
					writer.writeEndElement();
				}
			} catch (Exception e) {
				logger.error("writeObject", e);
			}
		}
	}

	/**
	 * Write runtime.
	 * 
	 * @param writer
	 *            the writer
	 * @throws XMLStreamException
	 *             the xML stream exception
	 */
	public void writeRuntime(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("availableProcessors");
		writer.writeCData(String.valueOf(SystemInfo.getAvailableProcessors()));
		writer.writeEndElement(); // end of availableProcessors
		writer.writeStartElement("freeMemory");
		writer.writeCData(String.valueOf(SystemInfo.getFreeMemory()));
		writer.writeEndElement(); // end of freeMemory
		writer.writeStartElement("maxMemory");
		writer.writeCData(String.valueOf(SystemInfo.getMaxMemory()));
		writer.writeEndElement(); // end of maxMemory
		writer.writeStartElement("totalMemory");
		writer.writeCData(String.valueOf(SystemInfo.getTotalMemory()));
		writer.writeEndElement(); // end of totalMemory
		// get thread info
		writer.writeStartElement("threadCount");
		writer.writeCData(String.valueOf(SystemInfo.getThreadCount()));
		writer.writeEndElement(); // end of threadCount
		writer.writeStartElement("daemonThreadCount");
		writer.writeCData(String.valueOf(SystemInfo.getDaemonThreadCount()));
		writer.writeEndElement(); // end of daemonThreadCount
		writer.writeStartElement("peakThreadCount");
		writer.writeCData(String.valueOf(SystemInfo.getPeakThreadCount()));
		writer.writeEndElement(); // end of peakThreadCount
	}
}
