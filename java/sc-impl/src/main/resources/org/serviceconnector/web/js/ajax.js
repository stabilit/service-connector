/*
  general ajax call back function
*/
function AjaxCallback() {             
  if( 4 == this.req.readyState ) {
    if( 200 != this.req.status ) {
      if (this.errorCall != null) {
         this.errorCall();
      }
    } else {           
      if (this.successCall != null) {      
         this.successCall();      
      }
    }
  }
}  
/*
  ajax init function
*/
function AjaxInit() {
  try {
    if( window.XMLHttpRequest ) {
      this.req = new XMLHttpRequest();
    } else if( window.ActiveXObject ) {
      this.req = new ActiveXObject( "Microsoft.XMLHTTP" );
    } else {
      alert( "Ihr Webbrowser unterstuetzt leider kein Ajax!" );
    }
    if( this.req.overrideMimeType ) {
        this.req.overrideMimeType( 'text/xml' );
    }
  } catch( e ) {
    alert( "Fehler: " + e );
  }
  return this.req;
}    
/*
  ajax call function
*/
function AjaxCall(path) {
  if (path != null) {
     this.path = path;
  }
  this.ajaxInit();
  if( this.req ) {
     var _this = this;
     this.req.open( "GET", this.path, true );
     this.req.onreadystatechange = function()
     {
        _this.ajaxCallback();
     }         
     this.req.send( null );
  }
}

function AjaxGetParam(key) {
  var responseText = this.req.responseText;
  var startKey = "<!--"+key+":";
  var start = responseText.indexOf(startKey);
  var end = responseText.indexOf(":"+key+"-->");
  var value = responseText.substr(start+startKey.length, end-(start+startKey.length));
  return value;
}

/*
  ajax call object
*/
function AjaxCallObject(key, path, successCall, errorCall)
{
  this.req = null;
  this.key = key;
  this.path = path;
  this.errorCall = errorCall;
  this.successCall = successCall;
  this.ajaxInit = AjaxInit;
  this.ajaxCall = AjaxCall;
  this.ajaxCallback = AjaxCallback;
  this.ajaxGetParam = AjaxGetParam;
}  
