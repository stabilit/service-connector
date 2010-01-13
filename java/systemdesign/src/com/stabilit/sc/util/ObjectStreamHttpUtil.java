package com.stabilit.sc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectStreamHttpUtil {

	public static Object readObject(InputStream is) throws Exception {
		DataInputStream dis = new DataInputStream(is);
		// read headers
		String line = dis.readLine();
		int contentLength = -1;
		while (line != null) {
			if (line.length() <= 0) {
				break;
			}
			// extract name from value
			int pos = line.indexOf(":");
			if (pos > 0) {
				String name = line.substring(0, pos);
				String value = line.substring(pos + 1, line.length());
				if ("content-length".equalsIgnoreCase(name)) {
					contentLength = Integer.parseInt(value.trim());
				}
			}
			line = dis.readLine();
		}
		if (contentLength <= 0) {
			return null;
		}
		byte[] stream = new byte[contentLength];
		int readBytes = dis.read(stream);
		ByteArrayInputStream bais = new ByteArrayInputStream(stream);
		// read object stream
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object obj = ois.readObject();
		return obj;
	}

	public static Object readObjectOnly(InputStream is) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(is);
		return ois.readObject();
	}

	public static void writeObjectOnly(OutputStream os, Object obj) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(obj);
		oos.flush();
	}

	public static void writeRequestObject(OutputStream os, String path,
			Object obj) throws Exception {
		DataOutputStream dos = new DataOutputStream(os);
//		POST / HTTP/1.1
//		User-Agent: Java/1.6.0_06
//		Host: 127.0.0.1:88
//		Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
//		Connection: keep-alive
//		Content-type: application/x-www-form-urlencoded
//		Content-Length: 357
		dos.writeBytes("POST / HTTP/1.1\r\n");
		dos.writeBytes("User-Agent: " + System.getProperty("java.runtime.version") + "\r\n");
		dos.writeBytes("Host: " + path + "\r\n");
		dos.writeBytes("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\r\n");
		dos.writeBytes("Connection: keep-alive\r\n");
		dos.writeBytes("Content-type: application/x-www-form-urlencoded\r\n");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		byte[] objStream = baos.toByteArray();
		dos.writeBytes("Content-Length: " + objStream.length + "\r\n");
		dos.writeBytes("\r\n");
		dos.flush();
		dos.write(objStream);
		dos.flush();
	}

	public static void writeResponseObject(OutputStream os, Object obj)
			throws Exception {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeBytes("HTTP/1.1 200 OK\r\n");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.flush();
		byte[] objStream = baos.toByteArray();
		dos.writeBytes("Content-Length: " + objStream.length + "\r\n");
		dos.writeBytes("\n");
		dos.write(objStream);
		dos.flush();
	}

}
