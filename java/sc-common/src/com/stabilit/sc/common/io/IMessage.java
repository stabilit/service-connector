package com.stabilit.sc.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public interface IMessage {
	public SCMPMsgType getKey();

	public IMessage newInstance();

	public Object getAttribute(String name);

	public void setAttribute(String name, Object value);

	Map<String, Object> getAttributeMap();

	public void encode(BufferedWriter bw) throws IOException;
	
	public void decode(BufferedReader br) throws IOException;

	public int getLength();
}
