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
package org.serviceconnector.web.netty;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.serviceconnector.web.IWebRequest;


/**
 * The Class NettyWebRequest.
 */
public class NettyWebRequest implements IWebRequest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyWebRequest.class);
	
	/** The request. */
	private HttpRequest request;
	
	/** The parameters. */
	private Map<String, List<String>> parameters;

	/**
	 * Instantiates a new netty web request.
	 *
	 * @param httpRequest the request
	 */
	public NettyWebRequest(HttpRequest httpRequest) {
		this.request = httpRequest;
		if (this.request != null) {
			QueryStringDecoder qsd = new QueryStringDecoder(this.getURL());
			this.parameters = qsd.getParameters();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebRequest#getURL()
	 */
	@Override
	public String getURL() {
		return this.request.getUri();
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebRequest#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String name) {
		List<String> paramList = this.parameters.get(name);
		if (paramList == null || paramList.size() <= 0) {
			return null;
		}
		return paramList.get(0);
	}

}
