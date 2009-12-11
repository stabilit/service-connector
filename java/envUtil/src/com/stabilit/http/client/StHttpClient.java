package com.stabilit.http.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StHttpClient {

	private static final int READ_TIMEOUT = 3000;
	private static final int MESSAGE_SIZE = 128;
	private static final String GET = "GET";
	private static String ip = "127.0.0.1";
	private static int port = 8000;
	private static int numberOfMessages = 1000;
	private URL url;
	private HttpURLConnection conn;
	private byte[] buffer;
	private int messCount;

	public StHttpClient(String ip, int port, int numberOfMessages) {
		StHttpClient.ip = ip;
		StHttpClient.port = port;
		StHttpClient.numberOfMessages = numberOfMessages;
		System.setProperty("http.keepAlive", "false");
		System.setProperty("http.maxConnections", "10");
	}

	private StHttpClient() {
	}

	public static void main(String[] args) {
		StHttpClient client = new StHttpClient();
		if (args.length != 0) {
			ip = args[0];
			port = Integer.valueOf(args[1]);
			numberOfMessages = Integer.valueOf(args[2]);
		}

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfMessages; i++) {
			client.send();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Total time neeeded: " + (endTime - startTime)
				+ " Ms");
	}

	public void send() {
		try {
			url = new URL("http://" + ip + ":" + port + "/");
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(GET);
			conn.setReadTimeout(READ_TIMEOUT);
			buffer = new byte[MESSAGE_SIZE];
			conn.connect();

			OutputStream writer = conn.getOutputStream();
			writer.write(buffer);
			writer.flush();
			writer.close();
			System.out.println("Message number " + messCount + " sent!");
			InputStreamReader inReader = new InputStreamReader(conn
					.getInputStream());
			BufferedReader in = new BufferedReader(inReader);

			String decodedString;

			// while ((decodedString = in.readLine()) != null) {
			// // System.out.println(decodedString);
			// }
			// System.out.println(in.toString());
			in.close();
			// System.out.println("Message number " + messCount + " sent!");
			messCount++;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			url = null;
			conn.disconnect();
			conn = null;
		}
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfMessages; i++) {
			send();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("Total time neeeded: " + (endTime - startTime)
				+ " Ms");
	}
}
