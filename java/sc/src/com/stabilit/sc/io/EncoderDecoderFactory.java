package com.stabilit.sc.io;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.io.impl.ObjectStreamEncoderDecoder;

public class EncoderDecoderFactory {

	private static Map<String, IEncoderDecoder> encoderDecoderMap = new HashMap<String, IEncoderDecoder>();

	static {
		// object stream encoder decoder
		IEncoderDecoder encoderDecoder = new ObjectStreamEncoderDecoder();
		encoderDecoderMap.put(ObjectStreamEncoderDecoder.class.getName(), encoderDecoder);
		encoderDecoderMap.put("default", encoderDecoder);
	}
	
	public static IEncoderDecoder newInstance() {
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
