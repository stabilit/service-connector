package com.stabilit.sc.app.server.http.impl;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpResponse implements IResponse {

	private MessageEvent event;
	private SCOP scop;
	private ISession session;
	
	
	public NettyHttpResponse(MessageEvent event) {
		this.scop = null;
		this.event = event;
		this.session = null;
	}

	public MessageEvent getEvent() {
		return event;
	}
	
	public ChannelBuffer getBuffer() throws Exception {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   ObjectStreamHttpUtil.writeObjectOnly(baos, this.scop);
	   byte[] buf = baos.toByteArray();
	   return ChannelBuffers.copiedBuffer(buf);
	}
	
	@Override
	public void setSession(ISession session) {
	   this.session = session;	
	}
	
	@Override
	public void setJobResult(IMessageResult jobResult) {
		if (jobResult == null) {
			return;
		}
		try {
			this.scop = new SCOP(jobResult);
			if (this.session != null) {
			   this.scop.setSessionId(this.session.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		
	}

}
