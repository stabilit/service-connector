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

import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.DumpUtility;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.cmd.sc.DefaultXMLLoaderFactory;

/**
 * The Class AjaxMaintenanceXMLLoader.
 */
public class AjaxMaintenanceXMLLoader extends AbstractXMLLoader {
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
				DefaultXMLLoaderFactory.LOGGER.error(e.toString());
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
				DefaultXMLLoaderFactory.LOGGER.error(e.toString());
			}
		}
		writer.writeEndElement(); // close service tag
		// get logs xml loader from factory
		LogsXMLLoader logsXMLLoader = (LogsXMLLoader) DefaultXMLLoaderFactory.getLoaderFactory().getXMLLoader("/logs");
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