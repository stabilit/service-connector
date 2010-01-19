package com.stabilit.sc.queue;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.management.DescriptorKey;

import com.stabilit.netty.http.server.HttpRequestHandler;

public class Request implements Serializable, RequestMXBean {
	private static final long serialVersionUID = 1L;
	private String requestString;
	private int requestNumber;
	private String requestName;
	private Date requestTime;
	private Map<String, String> keyValue;	
	private transient HttpRequestHandler handler;

	@ConstructorProperties( { "requestName", "requestTime", "requestNumber",
			"requestString", "keyValue" })
	public Request(String requestName, String requestString, int requestNumber,
			Date requestTime, Map<String, String> keyValue) {
		this.requestString = requestString;
		this.requestNumber = requestNumber;
		this.requestTime = requestTime;
		this.keyValue = keyValue;
		this.requestName = requestName;
	}

	public String getRequestString() {
		return requestString;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}

	public int getRequestNumber() {
		return requestNumber;
	}

	public void setRequestNumber(int requestNumber) {
		this.requestNumber = requestNumber;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public Map<String, String> getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(Map<String, String> keyValue) {
		this.keyValue = keyValue;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	
	@DescriptorKey(value="export")
	public HttpRequestHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpRequestHandler handler) {
		this.handler = handler;
	}

	@Override
	public String toString() {
		return "" + requestName + "  " + requestNumber + " - " + requestTime;
	}
}
