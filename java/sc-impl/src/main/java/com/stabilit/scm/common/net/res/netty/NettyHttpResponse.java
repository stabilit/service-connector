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
package com.stabilit.scm.common.net.res.netty;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.log.Loggers;
import com.stabilit.scm.common.net.EncoderDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.scmp.ResponseAdapter;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class NettyHttpResponse is responsible for writing a response to a ChannelBuffer. Encodes scmp to a Http
 * frame. Based on JBoss Netty.
 */
public class NettyHttpResponse extends ResponseAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyHttpResponse.class);
	
	/** The Constant connectionLogger. */
	protected final static Logger connectionLogger = Logger.getLogger(Loggers.CONNECTION.getValue());
	
	/** The event from Netty framework. */
	private ChannelEvent event;
	/** The encoder decoder. */
	private IEncoderDecoder encoderDecoder;

	/**
	 * Instantiates a new netty http response.
	 * 
	 * @param event
	 *            the event from Netty Framework
	 */
	public NettyHttpResponse(ChannelEvent event) {
		this.scmp = null;
		this.event = event;
	}

	/**
	 * Gets the event.
	 * 
	 * @return the event
	 */
	public ChannelEvent getEvent() {
		return event;
	}

	/**
	 * Gets the buffer. Encodes the scmp.
	 * 
	 * @return the buffer
	 * @throws Exception
	 *             the exception
	 */
	public ChannelBuffer getBuffer() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EncoderDecoderFactory encoderDecoderFactory = EncoderDecoderFactory.getCurrentEncoderDecoderFactory();
		encoderDecoder = encoderDecoderFactory.newInstance(this.scmp);
		encoderDecoder.encode(baos, this.scmp);
		byte[] buf = baos.toByteArray();
		return ChannelBuffers.copiedBuffer(buf);
	}

	/** {@inheritDoc} */
	@Override
	public void setSCMP(SCMPMessage scmp) {
		if (scmp == null) {
			return;
		}
		scmp.setIsReply(true);
		this.scmp = scmp;
	}

	/** {@inheritDoc} */
	@Override
	public void write() throws Exception {
		// Build the response object.
		HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ChannelBuffer buffer = getBuffer();
		httpResponse.setContent(buffer);
		httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		// Write the response.
		event.getChannel().write(httpResponse);
		//if (connectionLogger.isDebugEnabled()) connectionLogger.debug(this.logWrite());	//TODO TRN
		ConnectionPoint.getInstance().fireWrite(this,
				((InetSocketAddress) this.event.getChannel().getLocalAddress()).getPort(),
				buffer.toByteBuffer().array());
	}
}
