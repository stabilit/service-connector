package com.stabilit.sc.cmd.impl;

import java.io.File;
import java.io.FileFilter;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobResult;
import com.stabilit.sc.job.impl.FileSystemJob;
import com.stabilit.sc.job.impl.FileSystemJob.ACTION;

public class FileSystemCommand implements ICommand {

	@Override
	public String getKey() {
		return "filesystem";
	}

	@Override
	public ICommand newCommand() {
		return new FileSystemCommand();
	}

	@Override
	public void run(IRequest request, IResponse response)
			throws CommandException {
       IJob job = request.getJob();
       if ("filesystem".equals(job.getKey()) == false) {
    	   throw new CommandException("no filesystem job [key="+job.getKey()+"]");
       }
       FileSystemJob fileSystemJob = (FileSystemJob)job;
       ACTION action = fileSystemJob.getAction();
       IJobResult jobResult = new JobResult(job);
       if (ACTION.LIST == action) {
    	   String path = fileSystemJob.getPath();
    	   File[] fileList = getFiles(path); 
           jobResult.setReturn(fileList);    	   
       }
       System.out.println("FileSystemCommand.run(): job = " + job.toString());
       try {
		response.setJobResult(jobResult);
	} catch (Exception e) {
		throw new CommandException(e.toString());
	}
	}
	
	private File[] getFiles(String path) {
	    // This filter only returns directories
		File dir = new File(".");
	    FileFilter fileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.isDirectory();
	        }
	    };
	    File[] files = dir.listFiles(fileFilter);
	    return files;
	}

}
