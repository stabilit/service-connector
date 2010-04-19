package com.stabilit.sc.common.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.impl.EncodingDecodingException;

public interface IEncoderDecoder extends IFactoryable {

	public static final String HEADER_REGEX = "(RES|REQ|EXC) .*";
	public static final String UNESCAPED_EQUAL_SIGN_REGEX = "(.*)(?<!\\\\)=(.*)";
	public static final String ESCAPED_EQUAL_SIGN = "\\=";
	public static final String EQUAL_SIGN = "=";
	public static final String CHARSET = "UTF-8"; // TODO ISO gemäss doc

	public void encode(OutputStream os, Object obj) throws EncodingDecodingException;

	public Object decode(InputStream is) throws EncodingDecodingException;
}
