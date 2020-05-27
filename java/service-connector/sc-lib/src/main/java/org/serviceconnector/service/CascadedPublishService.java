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

import org.serviceconnector.casc.CascadedClient;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.server.CascadedSC;
import org.serviceconnector.util.XMLDumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CascadedPublishService.
 */
public class CascadedPublishService extends Service implements IPublishService {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CascadedPublishService.class);
	/** The subscription queue. */
	private PublishMessageQueue<SCMPMessage> publishMessageQueue;
	/** The cascaded sc. */
	protected CascadedSC cascadedSC;
	/** The cascaded client. */
	private CascadedClient cascClient;
	/** The no data interval seconds. */
	private int noDataIntervalSeconds = 0;

	/**
	 * Instantiates a new cascaded publish service.
	 *
	 * @param name the name
	 * @param cascadedSC the cascaded sc
	 * @param noDataIntervalSeconds the no data interval seconds
	 */
	public CascadedPublishService(String name, CascadedSC cascadedSC, int noDataIntervalSeconds) {
		super(name, ServiceType.CASCADED_PUBLISH_SERVICE);
		this.cascadedSC = cascadedSC;
		this.cascClient = new CascadedClient(cascadedSC, this);
		this.noDataIntervalSeconds = noDataIntervalSeconds;
		this.publishMessageQueue = new PublishMessageQueue<SCMPMessage>();
	}

	/** {@inheritDoc} */
	@Override
	public PublishMessageQueue<SCMPMessage> getMessageQueue() {
		return this.publishMessageQueue;
	}

	/**
	 * Sets the cascaded sc.
	 *
	 * @param cascadedSC the new cascaded sc
	 */
	public void setCascadedSC(CascadedSC cascadedSC) {
		this.cascadedSC = cascadedSC;
	}

	/**
	 * Gets the cascaded sc.
	 *
	 * @return the cascaded sc
	 */
	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}

	/**
	 * Gets the cascaded client. Synchronization avoids returning the client in renew process.
	 *
	 * @return the cascaded client
	 */
	public synchronized CascadedClient getCascClient() {
		return this.cascClient;
	}

	/**
	 * Gets the no data interval seconds.
	 *
	 * @return the no data interval seconds
	 */
	public int getNoDataIntervalSeconds() {
		return noDataIntervalSeconds;
	}

	/**
	 * Renew cascaded client. Synchronization avoids returning the client in renew process.
	 */
	public synchronized void renewCascadedClient() {
		LOGGER.trace("cascaded publish service renew cascaded client service=" + this.getName());
		this.cascClient = new CascadedClient(cascadedSC, this);
	}

	@Override
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("service");
		writer.writeAttribute("name", this.name);
		writer.writeAttribute("type", this.type.getValue());
		writer.writeAttribute("enabled", this.enabled);
		writer.writeAttribute("noiInSeconds", this.noDataIntervalSeconds);
		this.cascadedSC.dump(writer);
		this.publishMessageQueue.dump(writer);
		writer.writeEndElement(); // service
	}
}
