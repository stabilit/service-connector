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
package org.serviceconnector.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.serviceconnector.SCVersion;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.cmd.sc.DefaultXMLLoaderFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractXMLLoader.
 */
public abstract class AbstractXMLLoader implements IXMLLoader {

	/** The Constant XMLSDF. */
	public static final SimpleDateFormat XMLSDF = new SimpleDateFormat("yyyy-MM-dd");

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(DefaultXMLLoaderFactory.class);

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
	 * @return true, if is text
	 * {@inheritDoc}
	 */
	@Override
	public boolean isText() {
		return false;
	}
	
	/**
	 * Adds the meta.
	 *
	 * @param name the name
	 * @param value the value
	 * {@inheritDoc}
	 */
	@Override
	public void addMeta(String name, String value) {
		this.metaMap.put(name, value);
	}

	/**
	 * Adds the meta.
	 *
	 * @param map the map
	 * {@inheritDoc}
	 */
	@Override
	public void addMeta(Map<String, String> map) {
		this.metaMapList.add(map);
	}

	/**
	 * Load body.
	 *
	 * @param writer the writer
	 * @param request the request
	 * @throws Exception the exception
	 */
	public abstract void loadBody(XMLStreamWriter writer, IWebRequest request) throws Exception;

	/**
	 * Load body.
	 *
	 * @param writer the writer
	 * @param request the request
	 * @throws Exception the exception
	 */
	public void loadBody(Writer writer, IWebRequest request) throws Exception {
		
	}

	/**
	 * Load.
	 *
	 * @param request the request
	 * @param os the os
	 * @throws Exception the exception
	 * {@inheritDoc}
	 */
	@Override
	public final void load(IWebRequest request, OutputStream os)
			throws Exception {
		if (this.isText()) {
			OutputStreamWriter writer = new OutputStreamWriter(os);
			this.loadBody(writer, request);
			return;
		
		}
		IWebSession webSession = request.getSession(false);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(os);
		writer.writeStartDocument();
		writer.writeStartElement("sc-web");
		writer.writeStartElement("head");
		writer.writeStartElement("meta");
		writer.writeAttribute("creation",
				DateTimeUtility.getCurrentTimeZoneMillis());
		// write sc version
		writer.writeEndElement(); // close meta tag
		writer.writeStartElement("meta");
		writer.writeAttribute("scversion", SCVersion.CURRENT.toString());
		writer.writeEndElement(); // close meta tag
		writer.writeStartElement("meta");
		writer.writeAttribute("scstatus", "success");
		writer.writeEndElement(); // close meta tag
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
	 * @param writer the writer
	 * @throws XMLStreamException the xML stream exception
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
		writer.writeEndElement();  // end of runtime
		// write statistics info
		writer.writeStartElement("statistics");
		this.writeBean(writer, Statistics.getInstance());
		writer.writeEndElement();  // end of statistics
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
	 * @param writer the writer
	 * @param obj the obj
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeBean(XMLStreamWriter writer, Object obj)
			throws XMLStreamException {
		if (obj == null) {
			return;
		}
//		Field[] fields = obj.getClass().getDeclaredFields();
//		for (Field field : fields) {
//			String name = field.getName();
//			try {
//				writer.writeStartElement(name);
//				try {
//					Object value = BeanUtils.getProperty(obj, name);
//					if (value != null) {
//						writer.writeCData(value.toString());
//					}
//				} catch (Exception e) {
//					// we ignore this exception
//				}
//				writer.writeEndElement();
//			} catch (Exception e) {
//			}
//		}
		Method[] methods = obj.getClass().getMethods();
		for (Method method : methods) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (method.getParameterTypes() == null) {
				continue;
			}
			if (parameterTypes.length > 0) {
				continue;
			}
			String name = method.getName();
			if (name.startsWith("get") == false) {
				continue;
			}
			name = String.valueOf(name.charAt(3)).toLowerCase()
					+ name.substring(4);
			if ("class".equals(name)) {
				continue;
			}
			try {
				Object value = method.invoke(obj);
				if (value == obj) {
					continue;
				}
				if (value != null) {
					writer.writeStartElement(name);
					writer.writeCData(value.toString());
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
	 * @param writer the writer
	 * @throws XMLStreamException the xML stream exception
	 */
	public void writeRuntime(XMLStreamWriter writer) throws XMLStreamException {
		Runtime runtime = Runtime.getRuntime();
        writer.writeStartElement("availableProcessors");
        writer.writeCData(String.valueOf(runtime.availableProcessors()));
        writer.writeEndElement();
        writer.writeStartElement("freeMemory");
        writer.writeCData(String.valueOf(runtime.freeMemory()));
        writer.writeEndElement();
        writer.writeStartElement("maxMemory");
        writer.writeCData(String.valueOf(runtime.maxMemory()));
        writer.writeEndElement();
        writer.writeStartElement("totalMemory");
        writer.writeCData(String.valueOf(runtime.totalMemory()));
        writer.writeEndElement();
	}

	/**
	 * Load resource.
	 *
	 * @param name the name
	 * @return the input stream
	 */
	public InputStream loadResource(String name) {
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(name);
			if (is != null) {
				return is;
			}
			is = this.getClass().getResourceAsStream(name);
			if (is != null) {
				return is;
			}
			is = new FileInputStream(name);
			if (is != null) {
				return is;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the xML date as string.
	 *
	 * @param date the date
	 * @return the xML date as string
	 */
	public String getXMLDateAsString(Date date) {
		synchronized (XMLSDF) { // XMLSDF is not thread safe
			return XMLSDF.format(date);
		}		
	}

	/**
	 * Gets the xML date from string.
	 *
	 * @param date the date
	 * @return the xML date from string
	 */
	public Date getXMLDateFromString(String date) {
		synchronized (XMLSDF) { // XMLSDF is not thread safe
			try {
				return XMLSDF.parse(date);
			} catch (ParseException e) {
				return new Date();
			}
		}		
	}
	
	/**
	 * Gets the xML next date as string.
	 *
	 * @param date the date
	 * @return the xML next date as string
	 */
	public String getXMLNextDateAsString(Date date) {
		Calendar c = Calendar.getInstance();
		return this.getXMLDateAsString(new Date(date.getYear(), date.getMonth(), date.getDate()+1));
	}
	
	/**
	 * Gets the xML previous date as string.
	 *
	 * @param date the date
	 * @return the xML previous date as string
	 */
	public String getXMLPreviousDateAsString(Date date) {
		Calendar c = Calendar.getInstance();
		return this.getXMLDateAsString(new Date(date.getYear(), date.getMonth(), date.getDate()-1));
	}
}
