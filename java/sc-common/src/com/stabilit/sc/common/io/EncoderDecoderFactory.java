package com.stabilit.sc.common.io;

import com.stabilit.sc.common.factory.Factory;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.impl.DefaultEncoderDecoder;
import com.stabilit.sc.common.io.impl.LargeMessageEncoderDecoder;

public class EncoderDecoderFactory extends Factory {

	private static EncoderDecoderFactory encoderDecoderFactory = new EncoderDecoderFactory();
	
	public static EncoderDecoderFactory getCurrentEncoderDecoderFactory() {
		return encoderDecoderFactory;
	}
	
	private EncoderDecoderFactory() {
		// object stream encoder decoder
		IEncoderDecoder encoderDecoder = new DefaultEncoderDecoder();
		this.add(DefaultEncoderDecoder.class.getName(), encoderDecoder);
		this.add("default", encoderDecoder);
		encoderDecoder = new LargeMessageEncoderDecoder();
		this.add(LargeMessageEncoderDecoder.class.getName(), encoderDecoder);
		this.add("large", encoderDecoder);
	}

	public IFactoryable newInstance() {
		return newInstance("default");
	}

	public IEncoderDecoder newInstance(SCMP scmp) {
		if (scmp.isPart()) {
			return newInstance("large");
		}
		if (scmp.isLargeMessage()) {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public IEncoderDecoder newInstance(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public IEncoderDecoder newInstance(String key) {
		IEncoderDecoder encoderDecoder = (IEncoderDecoder)super.newInstance(key);
		return encoderDecoder;
	}

	public Object[] getEncoderDecoders() {
		return this.factoryMap.keySet().toArray();
	}

}
