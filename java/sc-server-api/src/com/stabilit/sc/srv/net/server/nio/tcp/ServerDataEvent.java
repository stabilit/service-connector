package com.stabilit.sc.srv.net.server.nio.tcp;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;

class ServerDataEvent implements MessageEvent {
	public NioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}

	@Override
	public Object getMessage() {
		return ChannelBuffers.copiedBuffer(data);
	}

	@Override
	public SocketAddress getRemoteAddress() {		
		return socket.socket().getRemoteSocketAddress();
	}

	@Override
	public Channel getChannel() {		
		return (Channel) socket.socket().getChannel();
	}

	@Override
	public ChannelFuture getFuture() {		
		return null;
	}
}