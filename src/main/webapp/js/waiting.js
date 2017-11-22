var load = null;
function begin() {
	if(load == null){
		load = new Loading()
	}
    load.init();
    load.start();
//    alert("开始");
}

function stop() {
	if(load!=null){
//		alert("停止");
	    load.stop();
	    load = null;
	}
}