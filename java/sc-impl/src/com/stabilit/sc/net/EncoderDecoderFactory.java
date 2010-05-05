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
package com.stabilit.sc.net;

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.SCMP;

public class EncoderDecoderFactory extends Factory {

	private static EncoderDecoderFactory encoderDecoderFactory = new EncoderDecoderFactory();
	
	public static EncoderDecoderFactory getCurrentEncoderDecoderFactory() {
		return encoderDecoderFactory;
	}
	
	private EncoderDecoderFactory() {
		// object stream encoder decoder
		IEncoderDecoder encoderDecoder = new DefaultEncoderDecoder();
		this.add(DefaultEncoderDecoder.class.getName(), encoderDecoder);
		this.add("default", encoderDecoder);
		encoderDecoder = new LargeMessageEncoderDecoder();
		this.add(LargeMessageEncoderDecoder.class.getName(), encoderDecoder);
		this.add("large", encoderDecoder);
	}

	public IFactoryable newInstance() {
		return newInstance("default");
	}

	public boolean isLarge(SCMP scmp) {
		if (scmp.isPart() || scmp.isBodyOffset()) {
			return true;
		}
		if (scmp.isLargeMessage()) {
			return true;
		}
		return false;
	}
	public IEncoderDecoder newInstance(SCMP scmp) {
		if (scmp.isPart() || scmp.isBodyOffset()) {
			return newInstance("large");
		}
		if (scmp.isLargeMessage()) {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public IEncoderDecoder newInstance(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			return newInstance("large");
		}
		return newInstance("default");
	}

	public IEncoderDecoder newInstance(String key) {
		IEncoderDecoder encoderDecoder = (IEncoderDecoder)super.newInstance(key);
		return encoderDecoder;
	}

	public Object[] getEncoderDecoders() {
		return this.factoryMap.keySet().toArray();
	}
}
