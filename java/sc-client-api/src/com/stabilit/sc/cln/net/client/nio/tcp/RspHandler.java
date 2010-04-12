package com.stabilit.sc.cln.net.client.nio.tcp;

public class RspHandler {
	private byte[] rsp = null;

	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}

	public synchronized byte[] waitForResponse() {
		while (this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}

		return rsp;
	}
}
