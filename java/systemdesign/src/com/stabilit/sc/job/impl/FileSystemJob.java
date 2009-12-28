package com.stabilit.sc.job.impl;

import com.stabilit.sc.job.Job;

public class FileSystemJob extends Job {
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
