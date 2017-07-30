$("#startTime").attr("placeholder","2017-07-01 12:00:00");
$("#endTime").attr("placeholder",new Date().format("yyyy-MM-dd hh:mm:ss"));

function miningByTimeRange(){
	var ds = $("#startTime");
	var de = $("#endTime");
	var startTime = ds.val() == "" ? ds.attr("placeholder") : ds.vale();
	var endTime = de.val() == "" ? de.attr("placeholder") : de.val();
	
	$.ajax({
		type : "post",
		url : "/topic/miningByTimeRange",
		data : {
			topicId : getCookie("topicId"),
			startTime : startTime,
			endTime : endTime
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				console.log(msg.result);
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert(msg.result);
		}
	});
}