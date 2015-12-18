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
package org.serviceconnector.service;

import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class CascadedFileService.
 */
public class CascadedFileService extends Service {

	/** The cascaded sc. */
	protected CascadedSC cascadedSC;

	/**
	 * Instantiates a new cascaded file service.
	 * 
	 * @param name
	 *            the name
	 * @param cascadedSC
	 *            the cascaded sc
	 */
	public CascadedFileService(String name, CascadedSC cascadedSC) {
		super(name, ServiceType.CASCADED_FILE_SERVICE);
		this.cascadedSC = cascadedSC;
	}

	/**
	 * Gets the cascaded sc.
	 * 
	 * @return the cascaded sc
	 */
	public CascadedSC getCascadedSC() {
		return this.cascadedSC;
	}

	@Override
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("service");
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("type", this.type.getValue());
		writer.writeAttribute("enabled", this.enabled);
		this.cascadedSC.dump(writer);
		writer.writeEndElement(); // service
	}
}
