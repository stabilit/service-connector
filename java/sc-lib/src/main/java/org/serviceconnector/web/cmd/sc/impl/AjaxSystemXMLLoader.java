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
import java.io.Writer;
import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.cache.CacheManager;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.server.FileServer;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.DumpUtility;
import org.serviceconnector.util.SystemInfo;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.cmd.sc.DefaultXMLLoaderFactory;
import org.serviceconnector.web.cmd.sc.WebCommandException;
import org.serviceconnector.web.cmd.sc.XSLTTransformerFactory;

/**
 * The Class AjaxSystemXMLLoader.
 */
public class AjaxSystemXMLLoader extends AbstractXMLLoader {

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
				DefaultXMLLoaderFactory.LOGGER.debug("run gc");
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
			if ("enableService".equals(action)) {
				enableService(writer, request);
				return;
			}
			if ("disableService".equals(action)) {
				disableService(writer, request);
				return;
			}
			if ("dump".equals(action)) {
				DefaultXMLLoaderFactory.LOGGER.debug("SC dump by user interface");
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
				DefaultXMLLoaderFactory.LOGGER.debug("SC terminated by user interface");
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
				DefaultXMLLoaderFactory.LOGGER.debug("delete dump by user interface");
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
				DefaultXMLLoaderFactory.LOGGER.debug("clear cache by user interface");
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
				DefaultXMLLoaderFactory.LOGGER.debug("reset translet by user interface");
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
				DefaultXMLLoaderFactory.LOGGER.debug("download and replace configuration");
				downloadAndReplace(writer, request);
				writer.writeStartElement("status");
				writer.writeCharacters("success");
				writer.writeEndElement();
				return;
			}
			if ("uploadLogFiles".equals(action)) {
				DefaultXMLLoaderFactory.LOGGER.debug("upload current log files");
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

	/**
	 * Enable service.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	private void enableService(XMLStreamWriter writer, IWebRequest request) throws Exception {
		DefaultXMLLoaderFactory.LOGGER.debug("run disable service");
		String serviceName = request.getParameter("service");
		if (serviceName == null) {
			this.writeFailure(writer, "Missing service name!");
			return;
		}
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service service = serviceRegistry.getService(serviceName);
		if (service == null) {
			this.writeFailure(writer, "Can not enable service " + serviceName + ", not found!");
			return;		
		}
		service.setEnabled(true);
		this.writeSuccess(writer, "Service " + serviceName + " has been enabled!");
		return;
	}
	
	/**
	 * Disable service.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	private void disableService(XMLStreamWriter writer, IWebRequest request) throws Exception {
		DefaultXMLLoaderFactory.LOGGER.debug("run disable service");
		String serviceName = request.getParameter("service");
		if (serviceName == null) {
			this.writeFailure(writer, "Missing service name!");
			return;
		}
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		Service service = serviceRegistry.getService(serviceName);
		if (service == null) {
			this.writeFailure(writer, "Can not disable service " + serviceName + ", not found!");
			return;		
		}
		service.setEnabled(false);
		this.writeSuccess(writer, "Service " + serviceName + " has been disabled!");
		return;

	}

}