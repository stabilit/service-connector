package com.stabilit.sc.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCMP;

public class ObjectStreamEncoderDecoder implements IEncoderDecoder {

	public ObjectStreamEncoderDecoder() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void decode(InputStream is, Object obj) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = (ObjectInputStream) is;
		SCMP scmp = (SCMP) obj;
		Map<String, String> metaMap = (Map<String, String>) ois.readObject();
		scmp.setHeader(metaMap);
		try {
			Object body = ois.readObject();
			scmp.setBody(body);
		} catch (Exception e) {
		}
		return;
	}

	@Override
	public void encode(OutputStream os, Object obj) throws IOException {
		ObjectOutputStream oos = (ObjectOutputStream) os;
		SCMP scmp = (SCMP) obj;
		oos.writeObject(scmp.getHeader());
		Object body = scmp.getBody();
		if (body != null) {
			oos.writeObject(scmp.getBody());
		}
		return;
	}

}
