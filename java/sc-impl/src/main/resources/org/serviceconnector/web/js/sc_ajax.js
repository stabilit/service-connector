// service connector specific ajax scripts

function runGC() {
	ajaxSystem.ajaxCall('ajax/system?action=gc');	
}

function systemCallback() {
}

var ajaxSystem = new AjaxCallObject('System', 'ajax/system', systemCallback);

function timerCallback() {
	//alert(this.req.responseText);
	var scMeta = document.getElementById("sc_meta");
	if (scMeta != null) {
		scMeta.innerHTML = this.req.responseText;
	}
}

function timerCall() {
	ajaxTimer.ajaxCall();
}

var ajaxTimer = new AjaxCallObject('Timer', 'ajax/timer', timerCallback);

setInterval('timerCall()', 59000);

function resourceCallback() {
	var scResource = document.getElementById("sc_resource");
	if (scResource != null) {
		scResource.innerHTML = this.req.responseText;
	}
}

function resourceCall(name) {
	ajaxResource.ajaxCall('ajax/resource?name='+name);
}

var ajaxResource = new AjaxCallObject('Resource', 'ajax/resource', resourceCallback);

function infoCallback() {
	var scInfo = document.getElementById("sc_info");
	if (scInfo != null) {
		scInfo.innerHTML = this.req.responseText;
	}
}

function infoCall(name) {
	ajaxInfo.ajaxCall('ajax/info');
}

var ajaxInfo = new AjaxCallObject('Info', 'ajax/info', infoCallback);

setInterval('infoCall()', 5000);


