{
	var t_temp = new Date();
	t_temp.setHours(0);
	t_temp.setMinutes(0);
	t_temp.setSeconds(0);
	t_temp.setMilliseconds(0);
	var t_startTime = t_temp.getTime() - 30*24*60*60*1000;
	var tempS  = new Date(t_startTime);
	start = tempS.getFullYear()+"-"+(tempS.getMonth()+1)+"-"+tempS.getDate()+" "+tempS.getHours()+":"+tempS.getMinutes()+":"+tempS.getSeconds();            
	$("#startTime").attr("placeholder", start);
	$("#endTime").attr("placeholder", new Date().format("yyyy-MM-dd hh:mm:ss"));
}
$('.topicName').text("专题名称：" + getCookie("topicName"));
var fileBuf = null;
var stdfileId = null;
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
				async : false,
				crossDomain : true,
				url : "/extfile/checkExtfile",
				method : "POST",
				processData : false,
				contentType : false,
				dataType : "json",
				mimeType : "multipart/form-data",
				data : fd,
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
				error : function() {
					stop();
				}
			});
		} else {
			alert("文件[ " + filename + " ]不是excel文件。");
		}
	}, false);
});

/**
 * 根据时间范围查找标准文件。 *
 * 
 * @param startTime
 *            开始时间
 * @param endTime
 *            结束时间
 */
function queryStdfilesByTimeRange(startTime,endTime) {
	if(startTime == "" || startTime=="undefined" || endTime=="" || endTime=="undefined"){
		alert("时间选择有误");
		return ;
	}
	$.ajax({
		type : "POST",
		url : "/stdfile/queryStdfilesByTimeRange",
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
                        +'<td width="182px;" data-id="'+item.stdfileId+'" style="cursor: pointer;" title="'+item.stdfileName+'" onclick="showDetail(this)">'
                        +item.stdfileName
                        +'</td>'
                        +'<td width="179px;">'+new Date(item.uploadTime.time).format('yyyy-MM-dd hh:mm:ss')
                        +'</tr>';
					$("#extList").append(row);                        
				})
				$("#extList tr:first td:first").click();
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("查询历史上传记录失败！");
		}
	});
}

//显示标准文件的分类详情
function showDetail(e){
	stdfileId = $(e).attr("data-id");
	$.ajax({
		type : "post",
		url : "/stdfile/analyzeByStdfileId",
		data : {
			stdfileId : stdfileId
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				$('.summary_tab table tr:not(:first)').html('');
				var items = msg.result;
				for (var i = 0; i < items.length; i++) {
					// items第一行存储index，故从i+1读起
					var item = items[i];
					var rows = '<tr><td height="32" align="center">'+(i+1)+'</td><td height="32" align="center">' + item[0] + '</td><td height="32" align="center">' + item[2] + '</td><td height="32" align="center">'+
						'<a href="javascript:;" onclick="toPaint(' + i + ',\'' + item[0].replace(/\"/g, " ").replace(/\'/g, " ") + '\')">' + item[3] + '</a>'+
						'</td></tr>';
					$('.summary_tab table').append(rows);
				}		
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
 * 上传标准数据文件
 */
function uploadStd() {
	var form = new FormData();
	form.append("stdfile", fileBuf);
	form.append("topicId", getCookie("topicId"));
	$.ajax({
		async : false,
		crossDomain : true,
		url : "/stdfile/upload",
		method : "POST",
		processData : false,
		contentType : false,
		mimeType : "multipart/form-data",
		dataType : "json",
		data : form,
		success : function(msg) {
			if (msg.status == "OK") {
				$("#drop_area").text("文件「 " + fileBuf.name + " 」上传成功。");
				$(".btn_del_all").attr("disabled", true);
				$(".btn_upl_all").attr("disabled", true);
				searchTimeChange();
			} else {
				alert("aa"+msg.result);
			}
		},
		error : function() {
			alert("上传失败!");
			stop();
		}
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

function showTime(e){
	jeDate({
		dateCell:"#"+$(e).attr("id"),
            format:"YYYY-MM-DD hh:mm:ss",
            isTime:true,
            minDate:"2016-01-01 00:00:00",
            trigger: "click"
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
            	end = date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            }
            break;
    }
    queryStdfilesByTimeRange(start,end);
}

$("#startTime").change(function(){
	if(index = $("input[name='searchTime']:checked").val() == '3')
		searchTimeChange();
})

$("#endTime").change(function(){
	if(index = $("input[name='searchTime']:checked").val() == '3')
		searchTimeChange();
})

//画图页面跳转
function toPaint(targetIndex, title) {
	setCookie('targetIndex', targetIndex);
	setCookie('title', title);
	setCookie('stdfileId',stdfileId);
	jumpto("data_results");
}