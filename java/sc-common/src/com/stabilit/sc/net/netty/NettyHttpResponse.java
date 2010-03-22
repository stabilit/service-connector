package com.stabilit.sc.net.netty;

import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.MessageEvent;

import com.stabilit.sc.io.EncoderDecoderFactory;
import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class NettyHttpResponse implements IResponse {

	private MessageEvent event;
	private SCMP scmp;
	private ISession session;
	private IEncoderDecoder encoderDecoder = EncoderDecoderFactory.newInstance();	
	
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
	   encoderDecoder.encode(baos, this.scmp);
	   System.out.println(baos.size());
	   
	   byte[] buf = baos.toByteArray();
	   
	   System.out.println(buf.length);
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
