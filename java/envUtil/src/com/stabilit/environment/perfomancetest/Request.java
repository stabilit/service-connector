package com.stabilit.environment.perfomancetest;


public class Request {
	
	private String message;
	private byte[] buffer = new byte[128];
	
	public Request(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
}
