// service connector specific ajax scripts

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

var ajaxTimer = new AjaxCallObject('Timer', 'timer', timerCallback);

setInterval('timerCall()', 59000);


