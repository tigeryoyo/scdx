{
	var t_temp = new Date();
	t_temp.setHours(0);
	t_temp.setMinutes(0);
	t_temp.setSeconds(0);
	t_temp.setMilliseconds(0);
	var t_startTime = t_temp.getTime() - 30*24*60*60*1000;
	var t_endTime = t_temp.getTime() + 24*60*60*1000-1000;
	var tempS  = new Date(t_startTime);
	$("#startTime").attr("placeholder", new Date(t_startTime).format("yyyy-MM-dd hh:mm:ss"));
	$("#endTime").attr("placeholder", new Date(t_endTime).format("yyyy-MM-dd hh:mm:ss"));
}
$('.topicName').text("专题名称：" + getCookie("topicName"));
var fileBuf = null;
var stdfileId = "stdfile_cluster_result";
/**
 * 拖拽
 */
$(function() {
	// 阻止浏览器默认行。
	$(document).on({
		dragleave : function(e) { // 拖离
			e.preventDefault();
		},
		drop : function(e) { // 拖后放
			e.preventDefault();
		},
		dragenter : function(e) { // 拖进
			e.preventDefault();
		},
		dragover : function(e) { // 拖来拖去
			e.preventDefault();
		}
	});
	// 用javascript来侦听drop事件，首先要判断拖入的文件是否符合要求，包括图片类型、大小等，
	// 然后获取本地图片信息，实现预览，最后上传
	var box = document.getElementById('drop_area'); // 拖拽区域
	box.addEventListener("drop", function(e) {
		e.preventDefault(); // 取消默认浏览器拖拽效果
		var fileList = e.dataTransfer.files; // 获取文件对象
		// 检测是否是拖拽文件到页面的操作
		if (fileList.length == 0 || fileList.length > 1) {
			alert("一次只能上传一份标准数据文件。");
			return false;
		}
		// 检测文件是不是excel文件
		var filename = fileList[0].name;
		if (filename.lastIndexOf("xls") !== -1 || filename.lastIndexOf("xlsx") !== -1) {
			var origfile = fileList[0];
			var fd = new FormData();
			fd.append("origfile", origfile);
			$.ajax({
	//			async : false,
				crossDomain : true,
				url : "/extfile/checkExtfile",
				method : "POST",
				processData : false,
				contentType : false,
				dataType : "json",
				mimeType : "multipart/form-data",
				data : fd,
				beforeSend : function() {
					begin();
					},
				success : function(msg) {
					if (msg.status == "OK") {
						$("#drop_area").text(filename);
						$(".btn_del_all").removeAttr("disabled");
						$(".btn_upl_all").removeAttr("disabled");
						fileBuf = origfile;
					} else {
						alert("文件「 " + filename + " 」属行行不符合规定。");
					}
				},
				error: function (jqXHR, textStatus, errorThrown) {
		            var status = jqXHR.status;
		            if(status == 0){
		            	alert(textStatus);
		            }else if(status == 200){
		            	alert("您没有权限使用该资源...");
		            }else if(status == 500){
		            	alert("文件预览失败！");
		            }
		            else{
		            	alert(textStatus);
		            }
		        },
				complete:function(){
					stop();
				}
			});
		} else {
			alert("文件[ " + filename + " ]不是excel文件。");
		}
	}, false);
});

/**
 * 查找最近一次上传的文件 *
 * 
 * @param startTime
 *            开始时间
 * @param endTime
 *            结束时间
 */
function queryLastedStdfile() {
	$.ajax({
		type : "POST",
		url : "/stdfile/queryStdfile",
		data : {
			topicId : getCookie("topicId"),
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				var file = msg.result;
				$("#fileName").html(file.stdfileName);
				$("#fileUpTime").html(new Date(file.uploadTime.time).format('yyyy-MM-dd hh:mm:ss'));
				var str = file.datatime.split(";");
				console.log(str);
				if(str.length>2){
					$("#dataStartTime").html(str[0]);
					$("#dataEndTime").html(str[str.length-2]);
				}else{
					$("#dataStartTime").html(str[0]);
					$("#dataEndTime").html(str[0]);
				}
				
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert(textStatus);
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else if(status == 500){
            	alert("查询最近一次上传的文件失败！");
            }
            else{
            	alert(textStatus);
            }
        },
		complete:function(){
			stop();
		}
	});
}


//根据时间显示数据分类详情
function queryStdDataByTimeRange(timeRangeType,startTime,endTime){
	console.log(timeRangeType)
	console.log(startTime)
	console.log(endTime)
	$.ajax({
		type : "post",
		url : "/stdfile/analyzeByTimeRange",
		data : {
			topicId : getCookie("topicId"),
			timeRangeType:timeRangeType,
			startTime : startTime,
			endTime : endTime
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$('.summary_tab table tr:not(:first)').html('');
				var items = msg.result;
				for (var i = 0; i < (items.length>300?300:items.length); i++) {
					// items第一行存储index，故从i+1读起
					var item = items[i];
					console.log(item);
					var rows = '<tr><td height="32" align="center">'+(i+1)+'</td><td height="32" align="center">' + item[0] + '</td><td height="32" align="center">' + item[2] + '</td><td height="32" align="center">'+
						'<a href="javascript:;" onclick="toPaint(' + i + ',\'' + item[0].replace(/\"/g, " ").replace(/\'/g, " ") + '\')">' + item[3] + '</a>'+
						'</td></tr>';
					$('.summary_tab table').append(rows);
				}
				var pieTitle = "'"+getCookie("topicName")+"'";
				var count = 0;
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					count += parseInt(item[3]);
				}
				var rows = '<tr><td height="32" align="center">共:'+(items.length)+'类</td><td height="32" align="center"></td><td height="32" align="center"></td><td height="32" align="center">'+
				'<a href="javascript:;" onclick="toPaint(0,'+pieTitle+')">'+count+'</a>'+
				'</td></tr>';
				$('.summary_tab table').append(rows);
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert(textStatus);
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(textStatus);
            }
        },
		complete:function(){
			stop();
		}
	});
}


/**
 * 上传标准数据文件
 */
function uploadStd() {
	var form = new FormData();
	form.append("stdfile", fileBuf);
	form.append("topicId", getCookie("topicId"));
	$.ajax({
//		async : false,
		crossDomain : true,
		url : "/stdfile/upload",
		method : "POST",
		processData : false,
		contentType : false,
		mimeType : "multipart/form-data",
		dataType : "json",
		data : form,
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$("#drop_area").text("文件「 " + fileBuf.name + " 」上传成功。");
				$(".btn_del_all").attr("disabled", true);
				$(".btn_upl_all").attr("disabled", true);
				queryLastedStdfile();
			} else {
				alert("aa"+msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert(textStatus);
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else if(status == 500){
            	alert("文件上传失败！");
            }else{
            	alert(textStatus);
            }
        },
		complete: function(){
			stop();
		},
	});
}

/**
 * 根据resultId下载结果数据
 * 
 */
function downloadResultById() {
	$(function() {
		var form = $('<form method="POST" action="/stdfile/downloadStdfileByStdfileId">');
		form.append($('<input type="hidden" name="stdfileId" value="' + stdfileId + '"/>'));
		$('body').append(form);
		form.submit(); // 自动提交
	});
}

/**
 * 根据topicId与resultId下载报告
 * 
 */
function downloadReportById() {
	$(function() {
		var form = $('<form method="POST" action="/stdfile/downloadAbstractByStdfileId">');
		form.append($('<input type="hidden" name="topicId" value="' + getCookie("topicId") + '"/>'));
		form.append($('<input type="hidden" name="stdfileId" value="' + stdfileId + '"/>'));
		$('body').append(form);
		form.submit(); // 自动提交
	});
}

function showSTime(e){
	jeDate({
		dateCell:"#"+$(e).attr("id"),
            format:"YYYY-MM-DD 00:00:00",
            isTime:true,
            minDate:"2016-01-01 00:00:00",
            trigger: "click",
        })       
}
function showETime(e){
	jeDate({
		dateCell:"#"+$(e).attr("id"),
            format:"YYYY-MM-DD 23:59:59",
            isTime:true,
            minDate:"2016-01-01 00:00:00",
            trigger: "click",
        })       
}

/**
 * 删除待上传的标准数据文件
 */
function deleteStd() {
	fileBuf = null;
	$("#drop_area").text("将文件拖拽到此处");
	$(".btn_del_all").attr("disabled", true);
	$(".btn_upl_all").attr("disabled", true);
}

function searchTimeChange(){
	var index = $("input[name='searchTime']:checked").val();
    var startTime,endTime,start,end;
    var currentTime = new Date();
    endTime = currentTime.getTime();
    switch (index){
        case '1':
                var temp = new Date();
                temp.setTime(currentTime.getTime());
                temp.setHours(0);
                temp.setMinutes(0);
                temp.setSeconds(0);
                temp.setMilliseconds(0);
                startTime = temp.getTime() - 1*24*60*60*1000;
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
            startTime = temp.getTime() - 7*24*60*60*1000;
            var tempS  = new Date(startTime);
            var tempE = new Date(endTime);
            start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();
            end = tempE.getFullYear()+"-"+(tempE.getMonth()+1)+"-"+tempE.getDate()+" "+tempE.getHours()+":"+tempE.getMinutes()+":"+tempE.getSeconds();
            break;
        default :
            start = $("#startTime").val();
            end = $("#endTime").val();
            if(start=="" || start=="undefined" || null == start){
            	var temp = new Date();
                temp.setTime(currentTime.getTime());
                temp.setHours(0);
                temp.setMinutes(0);
                temp.setSeconds(0);
                temp.setMilliseconds(0);
                startTime = temp.getTime() - 30*24*60*60*1000;
                var tempS  = new Date(startTime);
                start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();
            }
            if(end == "" || end == "undefined" || null == end){
            	var date = new Date();
            	date.setHours(0);
            	date.setMinutes(0);
            	date.setSeconds(0);
            	date.setMilliseconds(0);
            	var t_endTime = date.getTime() + 24*60*60*1000-1000;
            	end = new Date(t_endTime).format("yyyy-MM-dd hh:mm:ss");
            }
            break;
    }
    queryStdDataByTimeRange(index,start,end);
}

//function timeChange(){
//	if((index = $("input[name='searchTime']:checked").val() == '3')&&($("#startTime").val()!="")&&($("#endTime").val()!=""))
//		searchTimeChange();
//}

//画图页面跳转
function toPaint(targetIndex, title) {
	setCookie('targetIndex', targetIndex);
	setCookie('title', title);
	setCookie('stdfileId',stdfileId);
	jumpto("data_results");
}