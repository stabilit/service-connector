/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/

package org.serviceconnector.web.cmd.sc.impl;

import javax.xml.stream.XMLStreamWriter;

import org.serviceconnector.conf.WebConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.registry.ServiceRegistry;
import org.serviceconnector.service.CascadedFileService;
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.Service;
import org.serviceconnector.web.AbstractXMLLoader;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.ctx.WebContext;

/**
 * The Class MaintenanceXMLLoader.
 */
public class MaintenanceXMLLoader extends AbstractXMLLoader {

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

	/**
	 * Load web configuration.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
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
		Boolean scTerminateAllowed = webConfiguration.isScTerminateAllowed();
		writer.writeStartElement("scTerminateAllowed");
		writer.writeCharacters(scTerminateAllowed.toString());
		writer.writeEndElement(); // end of scTerminateAllowed
		writer.writeEndElement(); // end of web-config
	}

	/**
	 * Load file services.
	 * 
	 * @param writer
	 *            the writer
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	private void loadFileServices(XMLStreamWriter writer, IWebRequest request) throws Exception {
		ServiceRegistry serviceRegistry = AppContext.getServiceRegistry();
		writer.writeStartElement("services");
		Service[] services = serviceRegistry.getServices();
		WebConfiguration webConfiguration = WebContext.getWebConfiguration();
		String scDownloadService = webConfiguration.getScDownloadService();
		String scUploadService = webConfiguration.getScUploadService();
		for (Service service : services) {
			if (service instanceof FileService || service instanceof CascadedFileService) {
				// check if upload or download is active for this service
				writer.writeStartElement("service");
				String fileServiceName = service.getName();
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

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return new MaintenanceXMLLoader();
	}

}