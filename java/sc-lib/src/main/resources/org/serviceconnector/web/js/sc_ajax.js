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
  service connector specific ajax scripts
 
  author: Daniel Schmutz

 */

function setStatusError() {
   	var obj = document.getElementById("sc_status_area_error");	
   	if (obj != null) {
		obj.style.visibility = "visible";
   	}
   	obj = document.getElementById("sc_status_area_success");	
   	if (obj != null) {
		obj.style.visibility = "hidden";
   	}
}

function setStatusSuccess() {
   	var obj = document.getElementById("sc_status_area_success");	
   	if (obj != null) {
		obj.style.visibility = "visible";
   	}
   	obj = document.getElementById("sc_status_area_error");	
   	if (obj != null) {
		obj.style.visibility = "hidden";
   	}
}

function errorCallback() {
   	setStatusError();
}

function runGC() {
	ajaxSystem.ajaxCall('ajax/system?action=gc');	
}

function systemCallback() {
   	setStatusSuccess();
}

var ajaxSystem = new AjaxCallObject('System', 'ajax/system', systemCallback, errorCallback);

function timerCallback() {
   	setStatusSuccess();
	//alert(this.req.responseText);
	var scMeta = document.getElementById("sc_meta");
	if (scMeta != null) {
		scMeta.innerHTML = this.req.responseText;
	}
}

function timerCall() {
	ajaxTimer.ajaxCall();
}

var ajaxTimer = new AjaxCallObject('Timer', 'ajax/timer', timerCallback, errorCallback);

setInterval('timerCall()', 59000);

function resourceCallback() {
   	setStatusSuccess();
	var scResource = document.getElementById("sc_resource");
	if (scResource != null) {
		scResource.innerHTML = this.req.responseText;
	}
}

function resourceCall(name) {
	ajaxResource.ajaxCall('ajax/resource?name='+name);
}

var ajaxResource = new AjaxCallObject('Resource', 'ajax/resource', resourceCallback, errorCallback);

function infoCallback() {
	var scInfo = document.getElementById("sc_info");
	if (scInfo != null) {
		scInfo.innerHTML = this.req.responseText;
	}
}

function infoCall(name) {
	ajaxInfo.ajaxCall('ajax/info');
}

var ajaxInfo = new AjaxCallObject('Info', 'ajax/info', infoCallback, errorCallback);

function contentCallback() {
	var scContent = document.getElementById("sc_content");
	if (scContent != null) {
		scContent.innerHTML = this.req.responseText;
	}
}

function contentCall(id, query) {
//	alert('ajax/content?id='+id + '&' + query);
	ajaxContent.ajaxCall('ajax/content?id='+id + '&' + query);
}

var ajaxContent = new AjaxCallObject('Content', 'ajax/content', contentCallback, errorCallback);

