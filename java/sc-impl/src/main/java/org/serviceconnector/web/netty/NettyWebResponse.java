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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.serviceconnector.web.IWebResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class NettyWebResponse.
 */
public class NettyWebResponse implements IWebResponse {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyWebResponse.class);
	
	/** The response. */
	private HttpResponse response;
	
	/** The os. */
	private ByteArrayOutputStream os;

	/**
	 * Instantiates a new netty web response.
	 *
	 * @param httpResponse the response
	 */
	public NettyWebResponse(HttpResponse httpResponse) {
		this.response = httpResponse;
		this.os = null;
	}
	
	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebResponse#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		if (this.os == null) {
			this.os = new ByteArrayOutputStream();
		}
		return this.os;
	}
	
	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebResponse#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		if (this.os == null) {
			return null;
		}
		return this.os.toByteArray();
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType) {
		this.response.addHeader("Content-Type", contentType);
	}

}
