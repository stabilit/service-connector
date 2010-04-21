package com.stabilit.sc.common.io;


public interface IResponse {

	public SCMP getSCMP();
	
	public void setSCMP(SCMP scmp);

	public void setSession(ISession session);
	
	public void write() throws Exception;
	
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder);
	
	public boolean isLarge();		

}
