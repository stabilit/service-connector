package com.stabilit.http.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StHttpController {

	private ThreadPoolExecutor pool;
	
	public static void main(String[] args) {
		StHttpController contr = new StHttpController();
		contr.runCase1();
	}
	
	public void runCase1() {
		pool = new ThreadPoolExecutor(10, 10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		//pool.execute(new StHttpClient("10.0.0.129",80,1000));
		//pool.execute(new StHttpClient("10.0.0.129",80,1000));
		//pool.execute(new StHttpClient("10.0.0.129",80,1000));
	}

}
