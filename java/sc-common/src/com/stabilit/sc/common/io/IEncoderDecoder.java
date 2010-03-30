package com.stabilit.sc.common.io;

import java.io.InputStream;
import java.io.OutputStream;

public interface IEncoderDecoder {

	public void encode(OutputStream os, Object obj) throws Exception;
	
	public Object decode(InputStream is) throws Exception;

}
