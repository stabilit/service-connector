package com.stabilit.sc.cmd.impl;

import java.io.File;
import java.io.FileFilter;

import com.stabilit.sc.app.server.IHTTPServerConnection;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.Message;
import com.stabilit.sc.msg.impl.FileSystemMessage;
import com.stabilit.sc.msg.impl.FileSystemMessage.ACTION;

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
		SCMP scmp = request.getSCMP();	
		String messageId = scmp.getMessageId();
		if (FileSystemMessage.ID.equals(messageId) == false) {
			throw new CommandException("no filesystem message [id=" + messageId + "]");
		}
		FileSystemMessage fileSystemMsg = (FileSystemMessage) scmp.getBody();
		ACTION action = fileSystemMsg.getAction();
		IMessage result = new Message();
		if (ACTION.LIST == action) {
			String path = fileSystemMsg.getPath();
			File[] fileList = getFiles(path);
			result.setAttribute("fileList", fileList);
		}
		System.out.println("FileSystemCommand.run(): msg = " + fileSystemMsg.toString());
		try {
			scmp.setBody(result);
			response.setSCMP(scmp);
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
