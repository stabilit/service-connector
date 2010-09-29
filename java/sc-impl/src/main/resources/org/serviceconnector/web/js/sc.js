// service connector javascripts

function setStyleOver(obj) {
	if (obj == null) {
		return;
	}
	if (obj.className != null) {
		obj.className = obj.className + "_over";
	}
}

function setStyleOut(obj) {
	if (obj == null) {
		return;
	}	
	if (obj.className != null) {
		obj.className = obj.className.replace("_over","");
	}
}



