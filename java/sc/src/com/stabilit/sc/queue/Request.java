package com.stabilit.sc.queue;


import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Request implements Serializable{
	private static final long serialVersionUID = 1L;
	private String requestString;
	private int requestNumber;
	private Date requestTime;
	private Map<String, String> keyValue;
	
	@ConstructorProperties({"requestTime", "requestNumber", "requestString", "keyValue"})
	public Request(String requestString, int requestNumber, Date requestTime, Map<String, String> keyValue) {		
		this.requestString = requestString;
		this.requestNumber = requestNumber;
		this.requestTime = requestTime;
		this.keyValue = keyValue;
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
}
