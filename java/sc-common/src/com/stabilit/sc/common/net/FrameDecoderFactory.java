/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
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
