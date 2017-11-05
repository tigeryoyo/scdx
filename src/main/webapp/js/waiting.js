var load = null;
function begin() {
    load = new Loading()
    load.init();
    load.start();
}

function stop() {
	if(load!=null){
	    load.stop();
	}
}