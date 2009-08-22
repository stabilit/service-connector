package com.stabilit.sc.client;

import java.net.URL;

import com.stabilit.sc.client.ftp.SCFtpClient;
import com.stabilit.sc.client.http.SCHttpClient;

public class SCClientFactory {

	public static IClient newInstance(String endPoint) {
		try {
			URL url = new URL(endPoint);
			String protocol = url.getProtocol();
			if ("http".equals(protocol)) {
				return new SCHttpClient(url);
			}
			if ("ftp".equals(protocol)) {
				return new SCFtpClient(url);
			}
		} catch(Exception e) {			
		}
		return null;
	}

}
