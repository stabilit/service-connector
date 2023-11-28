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
package org.serviceconnector.net.res.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.netty.NettySCMPFrameDecoder;

/**
 * A factory for creating NettyTcpResponderPipelineFactory objects.
 *
 * @author JTraber
 */
public class NettyTcpResponderPipelineFactory extends ChannelInitializer<SocketChannel> {

	/** {@inheritDoc} */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// logging handler
		ch.pipeline().addFirst("logger", new LoggingHandler());
		// responsible for reading until SCMP frame is complete
		ch.pipeline().addLast("framer", new NettySCMPFrameDecoder());
		// responsible for handling request
		ch.pipeline().addLast(AppContext.getSCWorkerThreadPool(), "handler", new NettyTcpResponderRequestHandler());
	}
}
