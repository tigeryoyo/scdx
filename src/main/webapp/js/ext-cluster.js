$("#startTime").attr("placeholder", "2016-01-01 00:00:00");
$("#endTime").attr("placeholder", new Date().format("yyyy-MM-dd hh:mm:ss"));
searchTimeChange();
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
function queryExtfilesByTimeRange(startTime,endTime) {
	if(startTime == "" || startTime=="undefined" || endTime=="" || endTime=="undefined"){
		alert("时间选择有误");
		return ;
	}
	$.ajax({
		type : "post",
		url : "/extfile/queryExtfilesByTimeRange",
		data : {
			topicId : getCookie("topicId"),
			startTime : startTime,
			endTime : endTime
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				$("#extList").html("");
				var items = msg.result;
				$.each(items,function(idx,item){
					row = '<tr>'
                        +'<td width="15px;">'
                        +'<input type="checkbox" name="orig_check" checked="checked">'
                        +'</td>'
                        +'<td width="177px;" title="'+item.extfileName+'">'
                        +item.extfileName
                        +'</td>'
                        +'<td width="169px;">'+new Date(item.uploadTime.time).format('yyyy-MM-dd hh:mm:ss')
                        +'</tr>';
					$("#extList").append(row);                        
				})
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
function queryResultByTimeRange(startTime,endTime) {
	$.ajax({
		type : "post",
		url : "/result/queryResultByTimeRange",
		data : {
			topicId : getCookie("topicId"),
			startTime : startTime,
			endTime : endTime
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				console.log(msg.result);
				/*$("#resultList").html("");
				var items = msg.result;
				$.each(items,function(idx,tiem){
					row = '<tr>'                     
                        +'<td class="hand" title="'+item.extfileName+'">'
                        +item.extfileName
                        +'</td>';
					$("#resultList").append(row);                        
				})*/
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

function searchTimeChange(){
	var index = $("input[name='searchTime']:checked").val();
    var startTime,endTime,start,end;
    var currentTime = new Date();
    endTime = currentTime.getTime();
    switch (index){
        case '1':
                startTime = endTime - 1*60*1000;
            var tempS  = new Date(startTime);
            var tempE = new Date(endTime);
             start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();
             end = tempE.getFullYear()+"-"+(tempE.getMonth()+1)+"-"+tempE.getDate()+" "+tempE.getHours()+":"+tempE.getMinutes()+":"+tempE.getSeconds();

            break;
        case '2':
                var temp = new Date();
                temp.setTime(currentTime.getTime());
                temp.setHours(0);
                temp.setMinutes(0);
                temp.setSeconds(0);
                temp.setMilliseconds(0);
                startTime = temp.getTime() - 24*60*60*1000;
            var tempS  = new Date(startTime);
            var tempE = new Date(endTime);
             start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();
             end = tempE.getFullYear()+"-"+(tempE.getMonth()+1)+"-"+tempE.getDate()+" "+tempE.getHours()+":"+tempE.getMinutes()+":"+tempE.getSeconds();

            break;
        case '3':
            var temp = new Date();
            temp.setTime(currentTime.getTime());
            temp.setHours(0);
            temp.setMinutes(0);
            temp.setSeconds(0);
            temp.setMilliseconds(0);
            startTime = temp.getTime() - 7*24*60*60*1000;
            var tempS  = new Date(startTime);
            var tempE = new Date(endTime);
             start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();
             end = tempE.getFullYear()+"-"+(tempE.getMonth()+1)+"-"+tempE.getDate()+" "+tempE.getHours()+":"+tempE.getMinutes()+":"+tempE.getSeconds();

            break;
        default :
            start = $("#startTime").val();
            end = $("#endTime").val();
            if(start=="" || start=="undefined"){
                start = "2016-01-01 00:00:00";
            }
            if(end == "" || end == "undefined"){
            	var date = new Date();
            	end = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            }
            break;
    }
    queryExtfilesByTimeRange(start,end);
}