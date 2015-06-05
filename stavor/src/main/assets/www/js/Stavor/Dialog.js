var Dialog = function (){
	
}

Dialog.showDialog = function(title, message, callbackOk){
	this.dialog = document.getElementById("DivDialogBackground");
	this.dialog_title = document.getElementById("DivDialogTitle");
	this.dialog_text = document.getElementById("DivDialogText");
	this.confirm_button = document.getElementById("DialogConfirmButton");
	this.cancel_button = document.getElementById("DialogCancelButton");
	this.ok_button = document.getElementById("DialogCloseButton");
	
	//Show dialog
	this.dialog_title.innerHTML = document.getElementById(title).innerHTML;
	if(message.constructor === Array){
		this.dialog_text.innerHTML = document.getElementById(message[0]).innerHTML +
									message[1] +
									document.getElementById(message[2]).innerHTML;
	}else{
		this.dialog_text.innerHTML = document.getElementById(message).innerHTML;
	}
	this.confirm_button.style.display = "none";
	this.cancel_button.style.display = "none";
	this.ok_button.style.display = "block";
	this.ok_button.onclick = function(){	
		callbackOk();
		$( "#DivDialogBackground" ).fadeOut( "fast", function() {});
	};
	
	$( "#DivDialogBackground" ).fadeIn( "fast", function() {});
}

Dialog.showConfirmDialog = function(title, message, callbackOk, callbackCancel){
	this.dialog = document.getElementById("DivDialogBackground");
	this.dialog_title = document.getElementById("DivDialogTitle");
	this.dialog_text = document.getElementById("DivDialogText");
	this.confirm_button = document.getElementById("DialogConfirmButton");
	this.cancel_button = document.getElementById("DialogCancelButton");
	this.ok_button = document.getElementById("DialogCloseButton");
	
	//Show confirm dialog
	this.dialog_title.innerHTML = document.getElementById(title).innerHTML;
	if(message.constructor === Array){
		this.dialog_text.innerHTML = document.getElementById(message[0]).innerHTML +
									message[1] +
									document.getElementById(message[2]).innerHTML;
	}else{
		this.dialog_text.innerHTML = document.getElementById(message).innerHTML;
	}
	this.ok_button.style.display = "none";
	this.cancel_button.style.display = "block";
	this.cancel_button.onclick = function(){
		callbackCancel();
		$( "#DivDialogBackground" ).fadeOut( "fast", function() {});
	};
	this.confirm_button.style.display = "block";
	this.confirm_button.onclick = function(){
		callbackOk();
		$( "#DivDialogBackground" ).fadeOut( "fast", function() {});
	};
	
	$( "#DivDialogBackground" ).fadeIn( "fast", function() {});
}