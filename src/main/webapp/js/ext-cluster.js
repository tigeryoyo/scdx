{
	$('.topicName').text("专题名称：" + getCookie("topicName"));
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
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$("#extList").html("");
				var items = msg.result;
				$.each(items,function(idx,item){
					row = '<tr>'
                        +'<td width="15px;">'
                        +'<input type="checkbox" name="orig_check" checked="checked" data-id="'+item.extfileId+'">'
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
			 alert("您没有权限使用该资源...");
			 stop();
		},
		complete:function(){
			stop();
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
				$("#resultList").html("");
				var items = msg.result;
				if(null == items || 0 == items.length)
					return ;
				$.each(items,function(idx,item){
					row = '<tr>'
                        +'<td class="hand" width="192px;" title="'+item.resName+'" data-id="'+item.resId+'" onclick="showHistoryResult(this)">'
                        +item.resName
                        +'</td>'
                        +'<td width="169px;">'+new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')
                        +'</tr>';
					$("#resultList").append(row);                        
				})
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			 alert("您没有权限使用该资源...");
		}
	});
}

/**
 * 根据ajax查询到的内容显示聚类结果
 * 
 * @param items
 *            ajxa返回的内容string[][]
 */
function showResultByContent(items){
	for (var i = 0; i < items.length; i++) {
		// items第一行存储index，故从i+1读起
		var item = items[i];
		var rows = '<tr><td height="32" align="left"><input type="checkbox" name="result_check" style="width:20px;height:20px" data-id="'+i+'" data-count="'+item[3]+'" class="' + i
			+ '"/>&nbsp;'
			+(i+1)+'</td><td height="32" align="center"><a href="javascript:;" onclick="showClusterDetails('
			// + item[indexOfUrl]
			// + '
			+ (i+1) + ',\''
			// + item[indexOfUrl]
			+ resultId + '\',' + item[3] + ')">' + item[0] + '</a></td><td height="32" align="center">' + item[2] + '</td><td height="32" align="center">'
			// 添加画图的代码为：'<a href="javascript:;" onclick="toPaint(' + i + ',\'' +
			// item[indexOfTitle].replace(/\"/g, " ").replace(/\'/g, " ") +
			// '\')">' + item[3] + '</a>'
			+  item[3]  + '</td></tr>';
		$('.summary_tab table').append(rows);
	}
}


// 显示历史聚类结果
function showHistoryResult(e){
	resultId = $(e).attr("data-id");
	getDisplayResultById();
}

/**
 * 根据id得到显示该次结果。（title、url、time、amount）
 * 
 * @param resultId
 *            结果id
 */
function getDisplayResultById() {
	$.ajax({
		type : "post",
		url : "/result/getDisplayResultById",
		data : {
			resultId : resultId
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				$('.summary_tab table tr:not(:first)').html('');
				var items = msg.result;
				showResultByContent(items);				
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			 alert("您没有权限使用该资源...");
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
	var extfileIds = new Array();
	$.each(	$("input[name='orig_check']:checked"),function(index,input){
		extfileIds.push($(input).attr("data-id"));
	});
	if(0 == extfileIds.length){
		alert("您没有选择任何文件，无法聚类！");
		return;
	}
	$.ajax({
		type : "post",
		url : "/extfile/miningByExtfileIds",
		traditional:true,
		data : {
			topicId : getCookie("topicId"),
			extfileIds : extfileIds
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$('.summary_tab table tr:not(:first)').html('');
				var items = msg.result.displayResult;
				resultId = msg.result.resultId;
				showResultByContent(items);
				// 更新历史聚类结果
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
			            if(start=="" || start=="undefined"  || null == start){
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
			    queryResultByTimeRange(start,end);				
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限使用该资源...");
			stop();
		},
		complete: function(){
			stop();
		},
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
				$('.summary_tab table tr:not(:first)').html('');
				var items = msg.result;
				showResultByContent(items);
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限使用该资源...");
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
	$.each(	$("input[name='result_check']:checked"),function(index,input){
		indices.push(parseInt($(input).attr("data-id")));
	});
	if(0 == indices.length){
		alert("请选选择，在进行合并操作！");
		return;
	}
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
				getDisplayResultById();
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			 alert("您没有权限使用该资源...");
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
	$.each(	$("input[name='result_check']:checked"),function(index,input){
		indices.push(parseInt($(input).attr("data-id")));
	});
	if(0 == indices.length){
		alert("请选选择，在进行合并操作！");
		return;
	}
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
				getDisplayResultById();
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			 alert("您没有权限使用该资源...");
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
			 alert("您没有权限使用该资源...");
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

function showTime(e){
	jeDate({
		dateCell:"#"+$(e).attr("id"),
            format:"YYYY-MM-DD hh:mm:ss",
            isTime:true,
            minDate:"2016-01-01 00:00:00",
            trigger: "click",
            choosefun:function(datas){
            	timeChange();
            },
            okfun:function(datas){
            	timeChange();
            }
        })       
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
            if(start=="" || start=="undefined"  || null == start){
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
    queryExtfilesByTimeRange(start,end);
    queryResultByTimeRange(start,end);
}

function timeChange(){
	if(index = $("input[name='searchTime']:checked").val() == '4')
		searchTimeChange();
}

// 聚类的各个类详细信息显示操作js
function showClusterDetails(index,resultId,count){
	var url = '';
	$("#clusterItemAll").prop("checked",false);
	$.ajax({
		type : "post",
		url : "/result/getClusterResult",
		data : {
			clusterIndex : index,
			resultId : resultId
		},
		dataType : "json",
		async: false,// 同步
		success : function(msg) {
			if (msg.status == "OK") {
				var items = msg.result;
				var indexOfUrl = parseInt(items[0][1]);// + 1
				if(items == null || items.length == 0){
					alert('没有记录');
					return;
				}else if(items.length == 1){
					url = items[i + 1][indexOfUrl];
					return;
				}
				var indexOfTitle = parseInt(items[0][0]) ;// + 1
				var indexOfTime = parseInt(items[0][2]) ;// + 1
				
				$('.details_tab table tr:not(:first)').remove();
				for (var i = 0; i < items.length - 1; i++) {
					// items第一行存储index，故从i+1读起
					item = items[i + 1];
					url = item[indexOfUrl];
					rows = '<tr><td height="32" align="center"><input type="checkbox" id="itemCheckbox" style="width:20px;height:20px" class="'
						+ i
						+ '"/></td><td height="32" align="center"><a href="'
						+ item[indexOfUrl]
						+ '" target="blank">'
						+ item[indexOfTitle]
						+ '</a></td><td height="32" align="center">'
						+ item[indexOfTime]
						+ '</td><td height="32" align="center">'
						+ '<button class="btn btn-danger delItem" type="button" id="'
						+ i
						+'" >删除</button></td></tr>';
				$('.details_tab table').append(rows);
				// 将类的id作为table的id
				$('.details_tab table').attr('id',index);
				}
			}else{
				alert(msg.result);				
			}
		},
		error : function(msg) {
			 $('#code').hide();
		     $('#goodcover').hide();
		     freshData();
// alert(msg.result);
		}
	});
	// 类中只有一个元素直接打开url
	if(url != '' && count == 1){
		window.open(url);
	}else if(url == ''){
		 $('#code').hide();
	     $('#goodcover').hide();
	     freshData();
	}else{
		 $('#code').center();
	     $('#goodcover').show();
	     $('#code').fadeIn();
	}
}
// 全选类中所有元素
$("#historyAll").click(function() {
	if (this.checked) {
		$("input[name='result_check']").prop("checked", true);
	} else {
		$("input[name='result_check']").prop("checked", false);
	}
});

// 快速选中
function selectClusters(){
	var count = $("#clusternum_input").val();
	$("input[name='result_check'][data-count='"+count+"']").prop("checked", true);
}

// 弹出框的样式
$(function() {
    // alert($(window).height());
    $('#closebt').click(function() {
        $('#code').hide();
        $('#goodcover').hide();
        freshData();
    });
	$('#goodcover').click(function() {
        $('#code').hide();
        $('#goodcover').hide();
        freshData();
    });
    /*
	 * var val=$(window).height(); var codeheight=$("#code").height(); var
	 * topheight=(val-codeheight)/2; $('#code').css('top',topheight);
	 */
    jQuery.fn.center = function(loaded) {
        var obj = this;
        body_width = parseInt($(window).width());
        body_height = parseInt($(window).height());
        block_width = parseInt(obj.width());
        block_height = parseInt(obj.height());

        left_position = parseInt((body_width / 2) - (block_width / 2) + $(window).scrollLeft());
        if (body_width < block_width) {
            left_position = 0 + $(window).scrollLeft();
        };

        top_position = parseInt((body_height / 2) - (block_height / 2));// +
																		// $(window).scrollTop());
        if (body_height < block_height) {
            top_position = 0 + $(window).scrollTop();
        };

        if (!loaded) {

            obj.css({
                'position': 'fixed'
            });
            obj.css({
                'top': ($(window).height() - $('#code').height()) * 0.5,
                'left': left_position
            });
            $(window).bind('resize', function() {
                obj.center(!loaded);
            });
            $(window).bind('scroll', function() {
                obj.center(!loaded);
            });

        } else {
            obj.stop();
            obj.css({
                'position': 'fixed'
            });
            obj.animate({
                'top': top_position
            }, 200, 'linear');
        }
    }

})