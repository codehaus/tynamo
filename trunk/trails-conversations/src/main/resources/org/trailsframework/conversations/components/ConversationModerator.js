var ConversationModerator = Class.create();

function notImplemented(notImplemented) {
	alert("Not yet implemented " + notImplemented);
}

ConversationModerator.prototype = {
	initialize: function(baseURI, suffixURI, keepAlive, endOnClose, idleCheckSeconds, warnBeforeSeconds, 
			warnBeforeOperationToCall, endedOperationToCall) {
		this.baseURI = baseURI;
		this.suffixURI = suffixURI;
		this.keepAlive = keepAlive;
		this.endOnClose = endOnClose;
		this.idleCheckSeconds = idleCheckSeconds;
		this.warnBeforeSeconds = warnBeforeSeconds;
		this.warnBeforeOperationToCall = warnBeforeOperationToCall;
		this.endedOperationToCall = endedOperationToCall;
		this.idleCheckId = null;
		
		//Event.observe(window, 'unload', this.end.bind(this) );
		
		if (idleCheckSeconds != null && idleCheckSeconds > 0) this.checkIdleNext(idleCheckSeconds);
		
	},

	checkIdle: function() {
		new Ajax.Request(this.baseURI + "checkidle" + this.suffixURI + this.keepAlive + '&warn=' + this.warnBeforeSeconds, {
			method: 'get',
			evalJSON:true,
			onSuccess: this.handleIdleCheckResult.bind(this)
		});
	},

	end: function() {
		if (!this.endOnClose) return;
		new Ajax.Request(this.baseURI + "end" + this.suffixURI + false, {
			method: 'get'
		});
	},
	
	refresh: function() {
		new Ajax.Request(this.baseURI + "refresh" + this.suffixURI + 'true', {
			method: 'get'
		});
	},

	checkIdleNext: function(nextCheck) {
		if (typeof(nextCheck) == 'undefined') return;
		if (this.idleCheckId != null) clearTimeout(this.idleCheckId);
		if (nextCheck == 0) nextCheck++;
		this.idleCheckId = setTimeout(this.checkIdle.bind(this), nextCheck * 1000);
	},
	
	callUp : function(operationToCall, param) {
		alert('operationToCall called ' + operationToCall);
	},
	
	warnOfEnd : function(inSeconds) {
		alert('The page will become idle soon...');
		this.refresh();
		/*
		Dialog.alert("The page will become idle in " + inSeconds, 
				{width:300, 
				height:100, 
				okLabel: "close", 
				ok:this.checkIdle.bind(this)
				}
		);
		*/ 	
	},
	
	handleIdleCheckResult: function(transport) {
		var nextCheck = transport.responseJSON.nextCheck;
		if (nextCheck <= 0 ) {
			if (this.endedOperationToCall != null) this.callUp(endedOperationToCall);
			return;
		}
		var warnFor = transport.responseJSON.warn;
		if (!isNaN(warnFor)) if (warnFor > 0) this.warnOfEnd(warnFor);

		this.checkIdleNext(nextCheck);
	}
	
}
