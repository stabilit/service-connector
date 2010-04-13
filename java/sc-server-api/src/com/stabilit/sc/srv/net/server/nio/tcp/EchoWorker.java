package com.stabilit.sc.srv.net.server.nio.tcp;

import java.io.ByteArrayInputStream;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.channel.Channel;

import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.impl.EncodingDecodingException;
import com.stabilit.sc.common.net.netty.NettyTcpRequest;
import com.stabilit.sc.common.net.netty.NettyTcpResponse;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;
import com.stabilit.sc.srv.registry.ServerRegistry;

public class EchoWorker implements Runnable {
	private List queue = new LinkedList();

	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}

	public void run() {
		ServerDataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ServerDataEvent) queue.remove(0);
			}
			IRequest tcpRequest = new NettyTcpRequest(dataEvent);
			NettyTcpResponse response = new NettyTcpResponse(dataEvent);
			try {
				Channel channel = dataEvent.getChannel();
				ServerRegistry serverRegistry = ServerRegistry.getCurrentInstance();
				serverRegistry.setThreadLocal(channel.getParent().getId());
				ICommand command = CommandFactory.getCurrentCommandFactory().newCommand(tcpRequest);
				command.run(tcpRequest, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Return to sender
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}
