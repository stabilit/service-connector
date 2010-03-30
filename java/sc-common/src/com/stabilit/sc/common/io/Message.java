package com.stabilit.sc.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.sc.common.io.impl.DefaultEncoderDecoder;

public class Message implements IMessage {

	private static final long serialVersionUID = -1763291531850424661L;

	private SCMPMsgType key;
	
	protected Map<String, Object> attrMap;

	public Message() {
		this(SCMPMsgType.UNDEFINED);
	}
	
	public Message(SCMPMsgType key) {
		this.key = key;
		this.attrMap = new HashMap<String, Object>();
	}

	@Override
	public IMessage newInstance() {
		return new Message();
	}
	
	@Override
	public SCMPMsgType getKey() {
		return key;
	}

	@Override
	public void encode(BufferedWriter bw) throws IOException {
		Map<String, Object> attrMap = this.getAttributeMap();
		Set<Entry<String, Object>> attrEntrySet = attrMap.entrySet();
		StringBuilder mb = new StringBuilder();
		mb.append(IMessage.class.getName());
		mb.append(DefaultEncoderDecoder.EQUAL_SIGN);
		mb.append(this.getClass().getName());
		mb.append("\n");
		for (Entry<String, Object> entry : attrEntrySet) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			/********* escaping *************/
			key = key.replace(DefaultEncoderDecoder.EQUAL_SIGN, DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN);
			value = value.replace(DefaultEncoderDecoder.EQUAL_SIGN, DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN);

			mb.append(key);
			mb.append(DefaultEncoderDecoder.EQUAL_SIGN);
			mb.append(value);
			mb.append("\n");
		}
		bw.write(mb.toString());		
	}
	
	@Override
	public void decode(BufferedReader br) throws IOException {
		while (true) {
			String line = br.readLine();
			if (line == null || line.length() <= 0) {
				break;
			}
			Pattern decodReg = Pattern.compile(DefaultEncoderDecoder.UNESCAPED_EQUAL_SIGN_REGEX);
			Matcher match = decodReg.matcher(line);
			if (match.matches() && match.groupCount() == 2) {
				/********* escaping *************/
				String key = match.group(1).replace(DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN, DefaultEncoderDecoder.EQUAL_SIGN);
				String value = match.group(2).replace(DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN, DefaultEncoderDecoder.EQUAL_SIGN);
				this.setAttribute(key, value);
			}
		}
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Msg [key=" + key + "]");
		for (String name : attrMap.keySet()) {
			sb.append(" ");
			sb.append(name);
			sb.append("=");
			sb.append(attrMap.get(name));
		}
		return sb.toString();
	}
	
	
	@Override
	public Map<String, Object> getAttributeMap() {
		return attrMap;
	}

	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}
}
