package com.stabilit.sc.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IEncoderDecoder {

	public void encode(OutputStream os, Object obj) throws IOException;
	
	public Object decode(InputStream is) throws IOException, ClassNotFoundException;

}
