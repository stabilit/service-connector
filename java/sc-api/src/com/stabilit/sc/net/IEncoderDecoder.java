/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import com.stabilit.sc.factory.IFactoryable;

public interface IEncoderDecoder extends IFactoryable {

	public static final String UNESCAPED_EQUAL_SIGN_REGEX = "(.*)(?<!\\\\)=(.*)";
	public static final String ESCAPED_EQUAL_SIGN = "\\=";
	public static final String EQUAL_SIGN = "=";
	public static final String CHARSET = "UTF-8"; // TODO ISO gem�ss doc
	public static final Pattern DECODE_REG = Pattern.compile(UNESCAPED_EQUAL_SIGN_REGEX);

	public void encode(OutputStream os, Object obj) throws Exception;

	public Object decode(InputStream is) throws Exception;
}
