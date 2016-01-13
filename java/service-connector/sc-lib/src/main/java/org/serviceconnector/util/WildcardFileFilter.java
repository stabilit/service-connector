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

package org.serviceconnector.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.serviceconnector.Constants;

/**
 * The Class WildcardFileFilter.
 */
public final class WildcardFileFilter implements FileFilter {

	/** The pattern. */
	private final Pattern pattern;

	/**
	 * Instantiates a new wildcard file filter.
	 * 
	 * @param path
	 *            the path
	 */
	public WildcardFileFilter(String path) {
		final String slashPrefix = "(?<=^|/)";
		final String slashPostfix = "(?=/|$)";
		final String notStarPrefix = "(?<!\\*)";
		final String notStarPostfix = "(?!\\*)";

		if (!path.contains(Constants.SLASH)) {
			path = "**/" + path;
		}
		Pattern wildcardTokens = Pattern.compile("(" + slashPrefix + Pattern.quote("**") + slashPostfix + ")|" + "("
				+ notStarPrefix + Pattern.quote("*") + notStarPostfix + ")|" + "(" + Pattern.quote("?") + ")");
		Matcher matcher = wildcardTokens.matcher(path);
		StringBuilder escaped = new StringBuilder();
		int endOfLastMatch = 0;
		while (matcher.find()) {
			escaped.append(path.substring(endOfLastMatch, matcher.start()));
			if (matcher.group(1) != null) {
				assert (matcher.group(2).equals("**"));
				escaped.append(".*");
			} else if (matcher.group(2) != null) {
				assert (matcher.group(2).equals("*"));
				escaped.append("[^/]*");
			} else if (matcher.group(3) != null) {
				assert (matcher.group(3).equals(Constants.QUESTION_MARK));
				escaped.append(".");
			} else {
				throw new AssertionError("No groups matched: " + matcher);
			}
			endOfLastMatch = matcher.end();
		}
		escaped.append(path.substring(endOfLastMatch));
		this.pattern = Pattern.compile(escaped.toString());
	}

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		return pattern.matcher(file.getPath().replace(File.separator, Constants.SLASH)).matches();
	}
}
