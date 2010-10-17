package org.serviceconnector.fileservice.http.put;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploadHttpPut {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://localhost/test/upload/upload.php?name=test.txt");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("PUT");
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setChunkedStreamingMode(2048); // enable streaming of HTTP
			// request
			// body
			httpCon.connect();
			OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
			for (int i = 0; i < 100; i++) {
				out.write("Resource content");
			}
			out.flush();
			out.close();
			httpCon.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
