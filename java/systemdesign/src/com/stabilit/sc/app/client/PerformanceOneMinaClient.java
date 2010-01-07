package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.impl.EchoJob;

// TODO: Auto-generated Javadoc
/**
 * The Class PerformanceOneClient.
 */
public class PerformanceOneMinaClient {

	/** The perf. */
	private static PerformanceOneMinaClient perf = new PerformanceOneMinaClient();

	/** The counter mina. */
	private int counterMina;

	/** The number of msg mina. */
	public static int numberOfMsgMina;

	/** The pool. */
	private ThreadPoolExecutor pool;

	/** The start time. */
	public static long startTime;
	
	private IClient client;

	/**
	 * Instantiates a new performance one client.
	 */
	private PerformanceOneMinaClient() {
	}

	/**
	 * Gets the single instance of PerformanceOneClient.
	 * 
	 * @return single instance of PerformanceOneClient
	 */
	public static PerformanceOneMinaClient getInstance() {
		return perf;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		numberOfMsgMina = Integer.valueOf(args[1]);
		perf.go(args[0], Integer.valueOf(args[1]));
	}

	/**
	 * Go.
	 * 
	 * @param server
	 *            the server
	 * @param numberOfMsg
	 *            the number of msg
	 */
	private void go(String server, int numberOfMsg) {

		pool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		pool.allowCoreThreadTimeOut(true);
		pool.setKeepAliveTime(1, TimeUnit.MILLISECONDS);
		startTime = System.currentTimeMillis();

		pool.execute(new EchoClientStarter(server, numberOfMsg));
	}

	/**
	 * The Class EchoClientStarter.
	 */
	public class EchoClientStarter implements Runnable {

		/** The key. */
		private String key;

		/** The number of msg. */
		private int numberOfMsg;
		

		/**
		 * Instantiates a new echo client starter.
		 * 
		 * @param key
		 *            the key
		 * @param numberOfMsg
		 *            the number of msg
		 */
		public EchoClientStarter(String key, int numberOfMsg) {
			this.key = key;
			this.numberOfMsg = numberOfMsg;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			String sURL = "http://localhost:85/";
			URL url = null;
			try {
				url = new URL(sURL);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}

			client = ClientFactory.newInstance(key);
			if (client == null) {
				client = ClientFactory.newInstance();
			}
			if (client == null) {
				System.out.println("no client available");
			}
			client.setEndpoint(url);

			int index = 0;

			try {
				// Thread.sleep(2000);
				client.connect();
				IJob job = new EchoJob();
				job.setAttribute("msg", "hello " + ++index);
				IJobResult result = client.sendAndReceive(job);
				// System.out.println(result.getJob());
				// client.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mina handler method.
	 */
	public synchronized boolean minaHandlerMethod() {
		counterMina++;
		if (counterMina == (numberOfMsgMina * 13)) {
			try {
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;			
		}
		return false;
	}
}
