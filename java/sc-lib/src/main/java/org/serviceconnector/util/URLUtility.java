/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.util;


/**
 * The Class URLUtility.
 */
public class URLUtility {

	public static char URL_SEPERATOR_CHAR = '/';
	public static String URL_SEPERATOR = "/";
	
	/**
	 * Make and return a valid url root path. This method replaces any file system separators
	 * by its url separator and verifies and sets all required slashes between both parts.
	 *
	 * @param path the path
	 * @param file the file
	 * @return the url path string
	 */
	public static String makePath(String path, String file) {
		StringBuilder sb = new StringBuilder();
		String urlPath = path.replace('\\', URL_SEPERATOR_CHAR);
		String urlFile = file.replace('\\', URL_SEPERATOR_CHAR);
		if (urlPath.startsWith(URL_SEPERATOR) == false) {
			sb.append(URL_SEPERATOR);
		}
		sb.append(urlPath);
		if (urlPath.endsWith(URL_SEPERATOR) == false) {
			if (urlFile.startsWith(URL_SEPERATOR) == false) {
				sb.append(URL_SEPERATOR);
			}			
		}
		sb.append(urlFile);
		return sb.toString();
	}
}
