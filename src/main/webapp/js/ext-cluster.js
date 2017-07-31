$("#startTime").attr("placeholder", "2017-07-01 12:00:00");
$("#endTime").attr("placeholder", new Date().format("yyyy-MM-dd hh:mm:ss"));

/**
 * 根据时间范围查找结果记录。
 */
function queryResultByTimeRange() {
	$.ajax({
		type : "post",
		url : "/result/queryResultByTimeRange",
		data : {
			topicId : getCookie("topicId"),
			startTime : "2017-07-01 12:00:00",
			endTime : "2018-07-01 12:00:00"
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

/**
 * 根据id得到显示该次结果。（title、url、time、amount）
 */
function getDisplayResultById() {
	$.ajax({
		type : "post",
		url : "/result/getDisplayResultById",
		data : {
			resultId : "036e71a8-8c5c-445a-bfdf-aa3bff767219"
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

/**
 * 根据时间范围聚类。
 */
function miningByTimeRange() {
	var ds = $("#startTime");
	var de = $("#endTime");
	var startTime = ds.val() == "" ? ds.attr("placeholder") : ds.vale();
	var endTime = de.val() == "" ? de.attr("placeholder") : de.val();

	$.ajax({
		type : "post",
		url : "/extfile/miningByTimeRange",
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
