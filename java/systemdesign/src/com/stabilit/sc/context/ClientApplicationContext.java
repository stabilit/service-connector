package com.stabilit.sc.context;

import java.net.URL;

public class ClientApplicationContext extends ApplicationContext {
	public ClientApplicationContext() {
	}

	public String getKey() {
		String key = "default";
		if (args.length > 0) {
			key = args[0];
		}
		return key;
	}

	public URL getURL() {
		String sURL = "http://localhost:80/";
		try {
			URL url = new URL(sURL);
			String[] args = this.getArgs();
			for (String string : args) {
				try {
					url = new URL(args[0]);
					sURL = args[0];
					url = new URL(sURL);
					return url;
				} catch (Exception e) {

				}
			}
			return url;
		} catch (Exception e) {
		}
		return null;
	}

}
