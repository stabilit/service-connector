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
   	obj = document.getElementById("sc_terminate");
   	if (obj != null) {
        obj.innerHTML = "Service Connector is terminated";   		
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

function enableService(sid, name) {
	var check = window.confirm("Enable service " + name + "?");
	if (check == false) {
		return;
    }
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=enableService&service=' + name);	
}

function disableService(sid, name) {
	var check = window.confirm("Disable service " + name + "?");
	if (check == false) {
		return;
    }
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=disableService&service=' + name);	
}

function runGC(sid) {
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=gc');	
}

function scDump(sid) {
	var overlayDiv = document.getElementById("overlay");
	overlayDiv.style.display = 'block';
	var dialogBox = document.getElementById("DialogBox");
	if (dialogBox != null) {
       dialogBox.innerHTML = getDialogText("... Please Wait ...");
       showLayer("DialogBox");
       centerLayer("DialogBox", 400, 400, 0, 0);
	}
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=dump');	
}

function scDumpDelete(sid) {
	var check = window.confirm("Delete all SC dump files! Are you sure?");
	if (check == false) {
		return;
    }
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=deleteDump');	
}

function terminateSC(sid) {
	var check = window.confirm("Terminate SC! Are you sure?");
	if (check == false) {
		return;
    }
	var terminateDiv = document.getElementById("sc_terminate");
	if (terminateDiv != null) {
		terminateDiv.innerHTML = "service connector is terminating ...";
	}
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=terminate');	
}

function clearCache(sid) {
	var check = window.confirm("Clear Cache! Are you sure?");
	if (check == false) {
		return;
    }
	var clearCacheDiv = document.getElementById("sc_cache_clear");
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=clearCache');	
}

function resetTranslet(sid) {
	var check = window.confirm("Reset Translet! Are you sure?");
	if (check == false) {
		return;
    }
	var resetTransletDiv = document.getElementById("sc_translet_reset");
	ajaxSystem.ajaxCall('ajax/system' + sid + '?action=resetTranslet');	
}

function getDialogText(msg) {
    callPending = true;
	var text = '<table border="0" cellspacing="0" cellpadding="0" width="100%" class="sc_dialog_table">';
	text += '<tr><th class="sc_dialog_table_header" style="width:20px;"> </th>';
	text += '<th class="sc_dialog_table_header">' + msg + '</th>';
	text += '</tr></table>';
	return text;
}

function getDialogException(exception) {
	var text = '<table border="0" cellspacing="0" cellpadding="0" width="100%" class="sc_dialog_table">';
	text += '<tr><th class="sc_dialog_table_header" style="width:20px;"> </th>';
	text += '<th class="sc_dialog_table_header">' + msg + '</th>';
	text += '</tr></table>';
	return text;
}

function downloadAndReplaceSelected(service) {
	// get all checkboxes
	var inputs = document.getElementsByTagName("input"); //or document.forms[0].elements;  
	var query = "";
	for (var i = 0; i < inputs.length; i++) {  
		if (inputs[i].type == "checkbox") {  
			if (inputs[i].checked) {
				query += "&file=" + inputs[i].id;
			}  
		}  
	}
	if (query == "") {
		alert("No files selected! Please select one or more files!");
		return;
	}
	var check = window.confirm("Download and Replace Selected! Are you sure?");
	if (check == false) {
		return;
    }
	var overlayDiv = document.getElementById("overlay");
	overlayDiv.style.display = 'block';
	var dialogBox = document.getElementById("DialogBox");
	if (dialogBox != null) {
       dialogBox.innerHTML = getDialogText("... Please Wait ...");
       showLayer("DialogBox");
       centerLayer("DialogBox", 400, 400, 0, 0);
	}
	ajaxSystem.ajaxCall('ajax/system?action=downloadAndReplace&service=' + service + query);	
}

function uploadLogFiles(service) {
	var check = window.confirm("Upload current log files! Are you sure?");
	if (check == false) {
		return;
    }
	var overlayDiv = document.getElementById("overlay");
	overlayDiv.style.display = 'block';
	var dialogBox = document.getElementById("DialogBox");
	if (dialogBox != null) {
       dialogBox.innerHTML = getDialogText("... Please Wait ...");
       showLayer("DialogBox");
       centerLayer("DialogBox", 400, 400, 0, 0);
	}
	ajaxSystem.ajaxCall('ajax/system?action=uploadLogFiles&service=' + service);		
}

function systemCallback() {
	callPending = false;
	var overlayDiv = document.getElementById("overlay");
	overlayDiv.style.display = '';
	var dialogBox = document.getElementById("DialogBox");
	if (dialogBox != null) {
       dialogBox.innerHTML = this.req.responseText;
       showLayer("DialogBox");
       centerLayer("DialogBox", 400, 400, 0, 0);
	}
	var action = this.ajaxGetParam("action");
	var service = this.ajaxGetParam("service");	
	var sid = this.ajaxGetParam("sid");	
	if (action == "enableService") {
        window.location.reload();        		
	}
	if (action == "disableService") {
        window.location.reload();        		
	}
	if (action == "downloadAndReplace") {
		if (service != null) {
		    maintenanceCall(sid, "sc_property_download", service);
		}
	}
	if (action == "uploadLogFiles") {
		if (service != null) {
		    maintenanceCall(sid, "sc_logs_upload", service);
		}
	}
	if (action == "dump") {
	    maintenanceCall(sid, "sc_dump_list");
	}
	if (action == "dumpCache") {
	    maintenanceCall(sid, "sc_dump_cache_list");
	}
	if (action == "deleteDump") {
	    maintenanceCall(sid, "sc_dump_list");
	}
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

function resourceCall(sid, name) {
	ajaxResource.ajaxCall('ajax/resource'+sid +'?name='+name);
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

function contentCall(sid, id, query) {
//	alert('ajax/content?id='+id + '&' + query);
	ajaxContent.ajaxCall('ajax/content' + sid + '?id='+id + '&' + query);
}

var ajaxContent = new AjaxCallObject('Content', 'ajax/content', contentCallback, errorCallback);


function maintenanceCallback() {
//    callPending = false;
//    hideLayer("DialogBox");
	var scMaintenance = document.getElementById("sc_maintenance");
	if (scMaintenance != null) {
		scMaintenance.innerHTML = this.req.responseText;
	}
}

function maintenanceCall(sid, action, service, query) {
//	var dialogBox = document.getElementById("DialogBox");
//	if (dialogBox != null) {
//       dialogBox.innerHTML = getDialogText("... Please Wait ...");
//       showLayer("DialogBox");
//       centerLayer("DialogBox", 400, 400, 0, 0);
//	}
	if (action == "sc_logs_upload" || action == "sc_property_download") {
		var scMaintenance = document.getElementById("sc_maintenance");
		if (scMaintenance != null) {
			scMaintenance.innerHTML = "... please wait ... <img src='ajaxloader.gif' width='24'></img> ... contacting the remote file system ";
		}
	}
	ajaxMaintenance.ajaxCall('ajax/maintenance' + sid + '?action=' + action + '&service='+service + '&' + query);
}

var ajaxMaintenance = new AjaxCallObject('Maintenanace', 'ajax/maintenance', maintenanceCallback, errorCallback);

