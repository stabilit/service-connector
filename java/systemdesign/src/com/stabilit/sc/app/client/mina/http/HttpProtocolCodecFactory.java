package com.stabilit.sc.app.client.mina.http;

import java.net.URL;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class HttpProtocolCodecFactory implements ProtocolCodecFactory {

	private final ProtocolEncoder encoder;
	private final ProtocolDecoder decoder;

	public HttpProtocolCodecFactory(URL url) {
		encoder = new HttpRequestEncoder(url);
		decoder = new HttpResponseDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}
}
