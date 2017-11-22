var load = null;
function begin() {
	if(load == null){
		load = new Loading()
		load.init();
	    load.start();
	}    
}

function stop() {
	if(load!=null){
	    load.stop();
	    load = null;
	}
}