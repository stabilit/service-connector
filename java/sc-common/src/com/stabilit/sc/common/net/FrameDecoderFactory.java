/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
*/
/**
 * 
 */
package com.stabilit.sc.common.net;

import com.stabilit.sc.common.factory.Factory;


/**
 * @author JTraber
 *
 */
public class FrameDecoderFactory extends Factory {

	private static FrameDecoderFactory decoderFactory = new FrameDecoderFactory();
	
	public static FrameDecoderFactory getCurrentInstance() {
		return decoderFactory;
	}
	
	private FrameDecoderFactory() {
		IFrameDecoder frameDecoder = new DefaultFrameDecoder();
		this.add("default", frameDecoder);
		frameDecoder = new HttpFrameDecoder();
		this.add("http", frameDecoder);
	}

	public static IFrameDecoder getDefaultFrameDecoder()
	{				
		return (IFrameDecoder) decoderFactory.newInstance("default");
	}

	public static IFrameDecoder getFrameDecoder(String key)
	{				
		return (IFrameDecoder) decoderFactory.newInstance(key);
	}
	
}
