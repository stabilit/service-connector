package com.stabilit.sc.app.server.netty.http;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class NettyHttpResponse implements IResponse {

	private MessageEvent event;
	private SCMP scmp;
	private ISession session;
	
	
	public NettyHttpResponse(MessageEvent event) {
		this.scmp = null;
		this.event = event;
		this.session = null;
	}

	public MessageEvent getEvent() {
		return event;
	}
	
	public ChannelBuffer getBuffer() throws Exception {
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
	   ObjectStreamHttpUtil.writeObjectOnly(baos, this.scmp);
	   byte[] buf = baos.toByteArray();
	   return ChannelBuffers.copiedBuffer(buf);
	}
	
	@Override
	public void setSession(ISession session) {
	   this.session = session;	
	}
	
	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		try {
			this.scmp = scmp;
			if (this.session != null) {
			   this.scmp.setSessionId(this.session.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		
	}

}
