package com.stabilit.sc.io.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.stabilit.sc.io.IEncoderDecoder;
import com.stabilit.sc.io.IMessage;
import com.stabilit.sc.io.SCMP;

public class DefaultEncoderDecoder implements IEncoderDecoder {

	private static final String SCMP_BODY_TYPE = "scmp-body-type";
	private static final String SCMP_BODY_LENGTH = "scmp-body-length";
	private static final String CHARSET = "UTF-8";

	public DefaultEncoderDecoder() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void decode(InputStream is, Object obj) throws IOException, ClassNotFoundException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		while (true) {
			String line = br.readLine(); // TODO
			if (line == null || line.length() <= 0) {
				break;
			}
			String[] t = line.split("=");
			if (t.length == 2) {
				metaMap.put(t[0], t[1]);
			}
		}
		String scmpBodyType = metaMap.get(SCMP_BODY_TYPE);
		String scmpBodyLength = metaMap.get(SCMP_BODY_LENGTH);
		TYPE scmpBodyTypEnum = TYPE.getEnumType(scmpBodyType);
		SCMP scmp = (SCMP) obj;
		scmp.setHeader(metaMap);
		try {
			if (scmpBodyTypEnum == TYPE.STRING) {
				int caLength = Integer.parseInt(scmpBodyLength);
				char[] caBuffer = new char[caLength];
				br.read(caBuffer);
				String bodyString = new String(caBuffer, 0, caLength);
				scmp.setBody(bodyString);
				return;
			}
			if (scmpBodyTypEnum == TYPE.MESSAGE) {
				String classLine = br.readLine();
				if (classLine == null) {
					return;
				}
				String[] t = classLine.split("=");
				if (IMessage.class.getName().equals(t[0]) == false) {
					return;
				}
				if (t.length != 2) {
					return;
				}
				Class messageClass = Class.forName(t[1]);
				IMessage message = (IMessage) messageClass.newInstance();
				while (true) {
					String line = br.readLine();
					if (line == null || line.length() <= 0) {
						break;
					}
					t = line.split("=");
					if (t.length == 2) {
						message.setAttribute(t[0], t[1]);
					}
				}
				scmp.setBody(message);
				return;
			}
			if (scmpBodyTypEnum == TYPE.ARRAY) {
				int baLength = Integer.parseInt(scmpBodyLength);
				byte[] baBuffer = new byte[baLength];
				is.read(baBuffer);
				scmp.setBody(baBuffer);
				return;
			}
		} catch (Exception e) {
		}
		return;
	}

	@Override
	public void encode(OutputStream os, Object obj) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMP scmp = (SCMP) obj;
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();
		String messageType = scmp.getMessageType(); // messageType is never null
		if (messageType.startsWith("REQ_")) {
			sb.append("REQ / SCMP/");
		} else if (messageType.startsWith("RES_")) {
			if (scmp.isFault()) {
				sb.append("EXC / SCMP/");			
			} else {
				sb.append("RES / SCMP/");			    
			}
		}
		sb.append(SCMP.VERSION);
		sb.append("\n");
		Set<Entry<String, String>> entrySet = metaMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append("\n");
		}
		Object body = scmp.getBody();
		if (body != null) {
			if (String.class == body.getClass()) {
				String t = (String) body;
				sb.append(SCMP_BODY_TYPE);
				sb.append("=");
				sb.append(TYPE.STRING.getType());
				sb.append("\n");
				sb.append(SCMP_BODY_LENGTH);
				sb.append("=");
				sb.append(String.valueOf(t.length()));
				sb.append("\n\n");
				bw.write(sb.toString());
				bw.write(t);
				bw.flush();
				return;
			}
			if (body instanceof IMessage) {
				sb.append(SCMP_BODY_TYPE);
				sb.append("=");
				sb.append(TYPE.MESSAGE.getType());
				sb.append("\n\n");
				bw.write(sb.toString());
				bw.flush();
				IMessage message = (IMessage) body;
				Map<String, Object> attrMap = message.getAttributeMap();
				Set<Entry<String, Object>> attrEntrySet = attrMap.entrySet();
				StringBuilder mb = new StringBuilder();
				mb.append(IMessage.class.getName());
				mb.append("=");
				mb.append(message.getClass().getName());
				mb.append("\n");
				for (Entry<String, Object> entry : attrEntrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					mb.append(key);
					mb.append("=");
					mb.append(value.toString());
					mb.append("\n");
				}
				bw.write(mb.toString());
				bw.flush();
				return;
			}
			if (body instanceof byte[]) {
				sb.append(SCMP_BODY_TYPE);
				sb.append("=");
				sb.append(TYPE.ARRAY.getType());
				sb.append("\n");
				byte[] ba = (byte[]) body;
				sb.append(SCMP_BODY_LENGTH);
				sb.append("=");
				sb.append(String.valueOf(ba.length));
				sb.append("\n\n");
				bw.write(sb.toString());
				bw.flush();
				os.write((byte[]) ba);
				os.flush();
				return;
			} else {
				throw new IOException("unsupported body type");
			}
		} else { //TODO verify with DANI, added because null bodies is allowed!
			bw.write(sb.toString());
			bw.flush();
		}
		return;
	}

	private static enum TYPE {
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
