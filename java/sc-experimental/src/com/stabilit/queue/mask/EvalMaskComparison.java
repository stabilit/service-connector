/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.queue.mask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JTraber
 */
public class EvalMaskComparison {

	public static void main(String[] args) {
		EvalMaskComparison eval = new EvalMaskComparison();
		eval.runByte();
		eval.runRegex();
		eval.runChar();
	}

	private void runRegex() {
		long startTime = System.currentTimeMillis();
		String mask = "000012100012832102FADF-----------X-----------";
		String msgMask = "0000121%%%%%%%%%%%%--------------X-----------";

		String msgRegex = msgMask.replace("%", ".");
		Pattern pat = Pattern.compile(msgRegex);

		Matcher m = null;

		for (int i = 0; i < 1000000; i++) {
			m = pat.matcher(mask);
			if (m.matches()) {
				continue;
			}
		}
		System.out.println("Regex needed time : " + (System.currentTimeMillis() - startTime) + " milis");
	}

	private void runByte() {
		long startTime = System.currentTimeMillis();
		String mask = "000012100012832102FADF-----------X-----------";
		String msgMask = "0000121%%%%%%%%%%%%--------------X-----------";

		byte[] maskByte = mask.getBytes();
		byte[] msgMaskByte = msgMask.getBytes();

		for (int i = 0; i < 1000000; i++) {
			for (int byteIndex = 0; byteIndex < msgMaskByte.length; byteIndex++) {
				if (msgMaskByte[byteIndex] == 0x25) {
					continue;
				}
				if (maskByte[byteIndex] == msgMaskByte[byteIndex]) {
					continue;
				}
			}
		}
		System.out.println("ByteR needed time : " + (System.currentTimeMillis() - startTime) + " milis");
	}

	private void runChar() {
		long startTime = System.currentTimeMillis();
		String mask = "000012100012832102FADF-----------X-----------";
		String msgMask = "0000121%%%%%%%%%%%%--------------X-----------";

		for (int i = 0; i < 1000000; i++) {
			for (int charIndex = 0; charIndex < mask.length(); charIndex++) {
				if (mask.charAt(charIndex) == 0x25) {
					continue;
				}
				if (mask.charAt(charIndex) == msgMask.charAt(charIndex)) {
					continue;
				}
			}
		}
		System.out.println("CharR needed time : " + (System.currentTimeMillis() - startTime) + " milis");
	}
}
