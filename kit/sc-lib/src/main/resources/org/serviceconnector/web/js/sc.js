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
/*
  service connector tool scripts
 
  author: Daniel Schmutz

 */

function setStyleOver(obj) {
	if (obj == null) {
		return;
	}
	if (obj.className != null) {
		obj.className = obj.className + "_over";
	}
	// check for any child elements
	var anchors = obj.getElementsByTagName("a");
	if (anchors != null) {
		for (var i = 0; i < anchors.length; i++) {
			var anchor = anchors[i];
			anchor.className = anchor.className + "_over";
		}
	}
	var tds = obj.getElementsByTagName("td");
	if (tds != null) {
		for (var i = 0; i < tds.length; i++) {
			var td = tds[i];
			td.className = td.className + "_over";
		}
	}
}

function setStyleOut(obj) {
	if (obj == null) {
		return;
	}	
	if (obj.className != null) {
		obj.className = obj.className.replace("_over","");
	}
	// check for any child elements
	var anchors = obj.getElementsByTagName("a");
	if (anchors != null) {
		for (var i = 0; i < anchors.length; i++) {
			var anchor = anchors[i];
			anchor.className = anchor.className.replace("_over","");			
		}
	}
	var tds = obj.getElementsByTagName("td");
	if (tds != null) {
		for (var i = 0; i < tds.length; i++) {
			var td = tds[i];
			td.className = td.className.replace("_over","");
		}
	}
}



