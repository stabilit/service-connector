package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.message.Message;

public class FileSystemJob extends Message {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3998019689531196867L;

	public enum ACTION {
		LIST, LOAD, SAVE, DELETE, CHANGEDIR, RENAME
	};
	
	public FileSystemJob() {
    	super("filesystem");
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
