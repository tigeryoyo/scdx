$(document).ready(function() {
	initShowPage(1);
});

/**
 * 初始化当前页
 */
function initShowPage(currentPage) {
	var listCount = 0;
	if ("undefined" == typeof (currentPage) || null == currentPage) {
		currentPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/topic/queryAllTopicCount",
		data : JSON.stringify(GetJsonData(currentPage)),
		dataType : "json",
		contentType : "application/json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$("#page").initPage(msg.result, currentPage, allData);
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(textStatus);
            }
        },
		complete:function(){
							stop();
						}
	})
}

/**
 * 搜索初始化
 * @param currenPage
 */
function initSearchPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    var obj1 = $("#stopword_search").val();
    $.ajax({
        type: "post",
        url: "/topic/queryAllTopicCount",
        data:JSON.stringify(SearchJsonData(currenPage)),
        dataType: "json",
		contentType:"application/json",
		beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,searchData);
            } else {
            	$('.ht_cont tr:not(:first)').html("");
                alert(msg.result);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(textStatus);
            }
        },
        complete:function(){
			stop();
		}
    })
}

/**
 * 显示当前页
 */
function allData(page) {
	search_click = false;
	$.ajax({
		type : "post",
		url : "/topic/queryAllTopic",
		data : JSON.stringify(GetJsonData(page)),
		dataType : "json",
		contentType : "application/json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				var items = msg.result.list;
				$('.ht_cont tr:not(:first)').html("");
				var count = 0;
				$.each(items, function(idx, item) {
					count++;
					row = '<tr><td height="40" align="center">' + ((page - 1) * 10 + count)
							+ '</td><td height="40" align="center"><a href="javascript:;" onclick="openTopic(' + "'" + item.topicId + "'"
							+ "," + "'" + item.topicName + "'" + ')">' + item.topicName + '</a></td><td height="40" align="center">'
							+ item.creator + '</td><td height="40" align="center">'
							+ new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss') + '</td><td height="40" align="center">'
							+ item.lastOperator + '</td><td height="40" align="center">'
							+ new Date(item.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')
							+ '</td><td height="40" align="center"><button type="button" class="btn btn-danger" onclick="deleteTopic('
							+ "'" + item.topicId + "'" + ')">删除</button></td></tr>'
					$('.ht_cont').append(row);
				});
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
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
 * 删除专题
 * 
 * @param topicId
 *            专题id
 */
function deleteTopic(topicId) {
	$.ajax({
		type : "post",
		url : "/topic/delete",
		data : {
			topicId : topicId
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				delCookie("topicId");
				delCookie("topicName");
				alert(msg.result);
				jumpto("topic-list-all");
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
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
 * 进入该专题
 */
function openTopic(topicId, topicName) {
	// 将topicId、topicName存入cookie
	setCookie("topicId", topicId);
	setCookie("topicName", topicName);
	jumpto("orig-upload");
}

function GetJsonData(page) {
	var myDate = new Date();
	myDate.setHours(0);
	myDate.setMinutes(0);
	myDate.setSeconds(0);
	myDate.setMilliseconds(0);
	var timeStamp = Date.parse(myDate) / 1000;
	myDate.setTime((timeStamp + 24 * 60 * 60) * 1000);
	end = myDate.getFullYear() + "-" + (myDate.getMonth() + 1) + "-" + (myDate.getDate());
	myDate.setTime((timeStamp - 90 * 24 * 60 * 60) * 1000);
	start = myDate.getFullYear() + "-" + (myDate.getMonth() + 1) + "-" + myDate.getDate();
	var json = {
		"topicId" : "",
		"topicName" : "",
		"topicType" : "",
		"creater" : "",
		"createStartTime" : start,
		"createEndTime" : end,
		"lastUpdateStartTime" : start,
		"lastUpdateEndTime" : end,
		"pageNo" : parseInt(page),
		"pageSize" : 10
	};
	return json;
}


//搜索 支持函数
function searchData(page){
    $.ajax({
        type:"post",
        url:"/topic/queryAllTopic",
        data:JSON.stringify(SearchJsonData(page)),
        dataType:"json",
        contentType:"application/json",
        beforeSend : function() {
			begin();
			},
        success:function(msg){
            if(msg.status=="OK"){
            	var items = msg.result.list;
				$('.ht_cont tr:not(:first)').html("");
				var count = 0;
				$.each(items, function(idx, item) {
					count++;
					row = '<tr><td height="40" align="center">' + ((page - 1) * 10 + count)
							+ '</td><td height="40" align="center"><a href="javascript:;" onclick="openTopic(' + "'" + item.topicId + "'"
							+ "," + "'" + item.topicName + "'" + ')">' + item.topicName + '</a></td><td height="40" align="center">'
							+ item.creator + '</td><td height="40" align="center">'
							+ new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss') + '</td><td height="40" align="center">'
							+ item.lastOperator + '</td><td height="40" align="center">'
							+ new Date(item.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')
							+ '</td><td height="40" align="center"><button type="button" class="btn btn-danger" onclick="deleteTopic('
							+ "'" + item.topicId + "'" + ')">删除</button></td></tr>'
					$('.ht_cont').append(row);
				});				
            }else{
            	$('.ht_cont tr:not(:first)').html("");
            	alert(msg.result);
            }

        } ,
        error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
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

function SearchJsonData(page) {
	// var obj = $('#ht_name').val();
	var cstart = $('#b_time').val();
	var cend = $('#o_time').val();
	if(!(cend==""|| cend=="null" || cend=="undefined")){
		var timestamp = Date.parse(new Date(cend))/1000;
		var endDate = new Date();
		endDate.setTime((timestamp+24*60*60)*1000);
		cend = endDate.getFullYear() + "-" + (endDate.getMonth()+1) + "-" + endDate.getDate();
	}
	var start = $('#lb_time').val();
	var end = $('#lo_time').val();
	if(!(end==""|| end=="null" || end=="undefined")){
		var timestamp2 = Date.parse(new Date(end))/1000;
		var endDate2 = new Date();
		endDate2.setTime((timestamp2+24*60*60)*1000);
		end = endDate2.getFullYear() + "-" + (endDate2.getMonth()+1) + "-" + endDate2.getDate();	
	}
	console.log("searchlist:"+cstart)
    console.log("searchlist:"+cend)
    console.log("searchlist:"+start)
    console.log("searchlist:"+end)
	var json = {
		"topicId" : "",
		"topicName" : $('#ht_name').val(),
		"topicType" : "",
		"creater" : "",
		"createStartTime" : cstart,
		"createEndTime" : cend,
		"lastUpdateStartTime" : start,
		"lastUpdateEndTime" : end,
		"pageNo" : parseInt(page),
		"pageSize" : 10
    };
    return json;
}
