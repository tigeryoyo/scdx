$("#startTime").attr("placeholder", "2017-07-01 12:00:00");
$("#endTime").attr("placeholder", new Date().format("yyyy-MM-dd hh:mm:ss"));

/**
 * 此次聚类结果id
 */
var resultId = "";

/**
 * 根据时间范围查找基础文件。 *
 * 
 * @param topicId
 *            专题id
 * @param startTime
 *            开始时间
 * @param endTime
 *            结束时间
 */
function queryExtfilesByTimeRange() {
	$.ajax({
		type : "post",
		url : "/extfile/queryExtfilesByTimeRange",
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
 * 根据时间范围查找结果记录。
 * 
 * @param topicId
 *            专题id
 * @param startTime
 *            开始时间
 * @param endTime
 *            结束时间
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
 * 
 * @param resultId
 *            结果id
 */
function getDisplayResultById() {
	alert(resultId);
	$.ajax({
		type : "post",
		url : "/result/getDisplayResultById",
		data : {
			resultId : resultId
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
 * 
 * @param topicId
 *            专题id
 * @param startTime
 *            开始时间
 * @param endTime
 *            结束时间
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
				resultId = msg.result.resultId;
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
 * 重置结果，如果resultId为空则不能重置
 * 
 * @param resultId
 *            专题id
 */
function resetResultById() {
	$.ajax({
		type : "post",
		url : "/result/resetResultById",
		data : {
			resultId : resultId
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
 * 根据索引合并聚类结果中的某些类,索引indices为顺序。
 * 
 * @param resultId
 *            结果id
 * @param indices
 *            顺序索引
 */
function combineResultItemsByIndices() {
	var indices = new Array();
	indices.push(1);
	indices.push(3);
	indices.push(5);
	$.ajax({
		type : "post",
		url : "/result/combineResultItemsByIndices",
		data : {
			resultId : resultId,
			indices : indices
		},
		dataType : "json",
		traditional : true,
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
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
 * 根据索引删除聚类结果中的某些类,索引indices为顺序。 *
 * 
 * @param resultId
 *            结果id
 * @param indices
 *            顺序索引
 */
function deleteResultItemsByIndices() {
	var indices = new Array();
	indices.push(1);
	indices.push(3);
	indices.push(5);
	$.ajax({
		type : "post",
		url : "/result/deleteResultItemsByIndices",
		data : {
			resultId : resultId,
			indices : indices
		},
		dataType : "json",
		traditional : true,
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
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
 * 删除索引为index的类簇内的指定数据集。
 * 
 * @param resultId
 *            结果id
 * @param index
 *            类簇索引
 * @param indices
 *            类簇内要删除的索引集合
 */
function deleteClusterItemsByIndices() {
	var index = 0;
	var indices = new Array();
	indices.push(1);
	indices.push(3);
	indices.push(5);
	$.ajax({
		type : "post",
		url : "/result/deleteClusterItemsByIndices",
		data : {
			resultId : resultId,
			index : index,
			indices : indices
		},
		dataType : "json",
		traditional : true,
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
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
 * 根据resultId下载结果数据
 * 
 */

function downloadResultById() {
	$(function() {
		var form = $('<form method="POST" action="/result/downloadResultById">');
		form.append($('<input type="hidden" name="resultId" value="' + resultId + '"/>'));
		$('body').append(form);
		form.submit(); // 自动提交
	});
}