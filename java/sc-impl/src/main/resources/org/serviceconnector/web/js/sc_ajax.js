// service connector specific ajax scripts

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

setInterval('infoCall()', 5000);


