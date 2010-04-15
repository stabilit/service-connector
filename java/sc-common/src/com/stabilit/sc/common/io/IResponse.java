package com.stabilit.sc.common.io;


public interface IResponse {

	public void setSCMP(SCMP scmp);

	public void setSession(ISession session);
	
	public void write() throws Exception;
	
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder);

}
