package com.stabilit.sc.msg.impl;

import com.stabilit.sc.msg.Message;

public class FileSystemMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7860922024005869265L;

	public enum ACTION {
		LIST, LOAD, SAVE, DELETE, CHANGEDIR, RENAME
	};

	public static String ID = "filesystem";

	public FileSystemMessage() {
    	super(ID);
    }
	
	public ACTION getAction() {
		return (ACTION)this.getAttribute("action");
	}
	
	public void setAction(ACTION action) {
		this.setAttribute("action", action);
	}
	
	public String getPath() {
		return (String)this.getAttribute("path");
	}
	
	public void setPath(String path) {
		this.setAttribute("path", path);
	}
}
