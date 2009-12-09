package com.stabilit.queue;

public class Request {
	
	private String message;
	private RequestType type;
	
	public Request(String message, RequestType type) {
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}
}
