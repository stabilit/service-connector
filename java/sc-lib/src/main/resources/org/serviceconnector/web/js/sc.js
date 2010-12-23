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

function showLayer(id) {
	if (document.layers) // NN4+
	{
		if (document.layers[id] == null) {
			return;
		}
		document.layers[id].visibility = "show";
	} else if (document.getElementById) // gecko(NN6) + IE 5+
	{
		var obj = document.getElementById(id);
		if (obj == null) {
			return;
		}
		obj.style.visibility = "visible";
	} else if (document.all) // IE 4
	{
		if (document.all[id] == null) {
			return;
		}
		document.all[id].style.visibility = "visible";
	}
}

function isLayerHidden(id) {
	if (document.layers) // NN4+
	{
		if (document.layers[id] == null) {
			return false;
		}
		if (document.layers[id].visibility == "show") {
			return false;
		}
		return true;
	} else if (document.getElementById) // gecko(NN6) + IE 5+
	{
		var obj = document.getElementById(id);
		if (obj == null) {
			return false;
		}
		if (obj.style.visibility == 'visible') {
			return false;
		}
		return true;
	} else if (document.all) // IE 4
	{
		if (document.all[id] == null) {
			return false;
		}
		if (document.all[id].style.visibility == "visible") {
			return false;
		}
		return true;
	}
	return true;
}

function hideLayer(id) {
	if (document.layers) // NN4+
	{
		if (document.layers[id] == null) {
			return;
		}
		document.layers[id].visibility = "hide";
	} else if (document.getElementById) // gecko(NN6) + IE 5+
	{
		var obj = document.getElementById(id);
		if (obj == null) {
			return;
		}
		obj.style.visibility = "hidden";
	} else if (document.all) // IE 4
	{
		if (document.all[id] == null) {
			return;
		}
		document.all[id].style.visibility = "hidden";
	}
}

function centerLayer(id, width, height, xmove, ymove) {
	var screenSize = getWindowSize();
	if (xmove == null) {
		xmove = 0;
	}
	if (ymove == null) {
		ymove = 0;
	}
	if (document.layers) // NN4+
	{
		if (document.layers[id] == null) {
			return;
		}
	} else if (document.getElementById) // gecko(NN6) + IE 5+
	{
		var obj = document.getElementById(id);
		if (obj == null) {
			return;
		}
		var left = (xmove + ((screenSize.w - width) / 2));
		var right = (-xmove + ((screenSize.w - width) / 2));
		obj.style.left = left + 'px';
		obj.style.right = right + 'px';
		var top = (ymove + ((screenSize.h - height) / 2));
		var bottom = (-ymove + ((screenSize.h - height) / 2));
		obj.style.top = top + 'px';
		obj.style.bottom = bottom + 'px';;
        obj.style.width = width;
        obj.style.height = height;
	} else if (document.all) // IE 4
	{
		if (document.add[id] == null) {
			return;
		}
	}
}

function ScreenSize(w, h) {
	this.w=w;
	this.h=h;
}

function getWindowSize() {
	var myWidth = 0, myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	return new ScreenSize(myWidth, myHeight);
}

function getTopWindowSize() {
	var myWidth = 0, myHeight = 0;
	if( typeof( top.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = top.innerWidth;
		myHeight = top.innerHeight;
	} else if( top.document.documentElement && ( top.document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = top.document.documentElement.clientWidth;
		myHeight = top.document.documentElement.clientHeight;
	} else if( top.document && ( top.document.clientWidth || top.document.clientHeight ) ) {
		//IE 4 compatible
		myWidth = top.document.clientWidth;
		myHeight = top.document.clientHeight;
	}
	return new ScreenSize(myWidth, myHeight);
}

