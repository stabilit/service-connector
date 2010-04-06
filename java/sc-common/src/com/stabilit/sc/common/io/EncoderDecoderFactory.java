package com.stabilit.sc.common.io;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.common.io.impl.DefaultEncoderDecoder;
import com.stabilit.sc.common.io.impl.LargeMessageEncoderDecoder;

public class EncoderDecoderFactory {

	private static Map<String, IEncoderDecoder> encoderDecoderMap = new HashMap<String, IEncoderDecoder>();

	static {
		// object stream encoder decoder
		IEncoderDecoder encoderDecoder = new DefaultEncoderDecoder();
		encoderDecoderMap.put(DefaultEncoderDecoder.class.getName(), encoderDecoder);
		encoderDecoderMap.put("default", encoderDecoder);
		encoderDecoder = new LargeMessageEncoderDecoder();
		encoderDecoderMap.put(LargeMessageEncoderDecoder.class.getName(), encoderDecoder);
		encoderDecoderMap.put("large", encoderDecoder);
	}
	
	public static IEncoderDecoder newInstance() {
		return newInstance("default");
	}

	public static IEncoderDecoder newInstance(SCMP scmp) {
		if (scmp.isPart()) {
			return newInstance("large");
		}
		if (scmp.isLargeMessage()) {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public static IEncoderDecoder newInstance(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public static IEncoderDecoder newInstance(String key) {
		IEncoderDecoder encoderDecoder = encoderDecoderMap.get(key);
		return encoderDecoder;
	}

	public static Object[] getEncoderDecoders() {
		return encoderDecoderMap.keySet().toArray();
	}
	
}
