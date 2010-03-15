package com.stabilit.sc.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IEncoderDecoder {

	public void encode(OutputStream os, Object obj) throws IOException;
	
	public void decode(InputStream is, Object obj) throws IOException, ClassNotFoundException;

}
