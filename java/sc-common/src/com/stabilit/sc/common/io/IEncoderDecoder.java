package com.stabilit.sc.common.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.stabilit.sc.common.io.impl.EncodingDecodingException;

public interface IEncoderDecoder {

	public static final String HEADER_REGEX = "(RES|REQ|EXC) / .*";
	public static final String UNESCAPED_EQUAL_SIGN_REGEX = "(.*)(?<!\\\\)=(.*)";
	public static final String ESCAPED_EQUAL_SIGN = "\\=";
	public static final String EQUAL_SIGN = "=";
	public static final String CHARSET = "UTF-8"; // TODO ISO gemäss doc

	public void encode(OutputStream os, Object obj) throws EncodingDecodingException;

	public Object decode(InputStream is) throws EncodingDecodingException;

	public static enum TYPE {
		UNDEFINED("undefined"), MESSAGE("msg"), ARRAY("array"), STRING("string");
		private String type = "undefined";

		private TYPE(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static TYPE getEnumType(String type) {
			if (UNDEFINED.getType().equals(type)) {
				return UNDEFINED;
			}
			if (STRING.getType().equals(type)) {
				return STRING;
			}
			if (MESSAGE.getType().equals(type)) {
				return MESSAGE;
			}
			if (ARRAY.getType().equals(type)) {
				return ARRAY;
			}
			return UNDEFINED;
		}
	}
}
