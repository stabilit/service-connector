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
package org.serviceconnector.net.res.netty;

import java.net.InetSocketAddress;

import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.res.ResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * The Class NettyHttpResponse is responsible for writing a response to a ChannelBuffer. Encodes scmp to a Http frame. Based on JBoss Netty.
 */
public class NettyHttpResponse extends ResponseAdapter {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpResponse.class);

	/**
	 * Instantiates a new netty http response.
	 *
	 * @param channel the communication channel
	 */
	public NettyHttpResponse(Channel channel) {
		super(channel);
	}

	/** {@inheritDoc} */
	@Override
	public void write() throws Exception {
		// Build the response object.
		ByteBuf buffer = getBuffer();
		FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
		httpResponse.headers().add("Access-Control-Allow-Origin", "*");
		httpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, scmp.getBodyType().getMimeType());
		httpResponse.headers().add(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
		httpResponse.headers().add(HttpHeaderNames.PRAGMA, HttpHeaderValues.NO_CACHE);
		httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(buffer.readableBytes()));
		if (ConnectionLogger.isEnabledFull()) {
			ConnectionLogger.logWriteBuffer(this.getClass().getSimpleName(), ((InetSocketAddress) this.channel.remoteAddress()).getHostName(),
					((InetSocketAddress) this.channel.remoteAddress()).getPort(), buffer.array(), 0, buffer.array().length);
		}
		// Write the response.
		channel.writeAndFlush(httpResponse);
	}
}
