package com.stabilit.sc.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;

import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.SCOP;

public class ObjectStreamEncoderDecoder implements IEncoderDecoder {

	public ObjectStreamEncoderDecoder() {
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void decode(InputStream is, Object obj) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = (ObjectInputStream) is;
        SCOP scop = (SCOP)obj;
		Map<String, String> metaMap = (Map<String, String>) ois.readObject();
		Object body = ois.readObject();
	    scop.setMetaMap(metaMap);
	    scop.setBody(body);
        return;
	}

	@Override
	public void encode(OutputStream os, Object obj) throws IOException {
        ObjectOutputStream oos = (ObjectOutputStream) os;
        SCOP scop = (SCOP)obj;
        oos.writeObject(scop.getMetaMap());
        oos.writeObject(scop.getBody());
        return;
	}

}
