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
		url : "/topic/queryOwnTopicCount",
		data : JSON.stringify(GetJsonData(currentPage)),
		dataType : "json",
		contentType : "application/json",
		success : function(msg) {
			if (msg.status == "OK") {
				$("#page").initPage(msg.result, currentPage, allData);
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("数据请求失败");
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
		url : "/topic/queryOwnTopic",
		data : JSON.stringify(GetJsonData(page)),
		dataType : "json",
		contentType : "application/json",
		success : function(msg) {
			if (msg.status == "OK") {
				var items = msg.result.list;
				$('.ht_cont tr:not(:first)').html("");
				var count = 0;
				$.each(items, function(idx, item) {
					count++;
					var item_topicId = "'" + item.topicId + "'";
					row = '<tr><td height="40" align="center">' + ((page - 1) * 10 + count)
							+ '</td><td height="40" align="center"><a href="javascript:;" onclick="openTopic(' + item_topicId + ')">'
							+ item.topicName + '</a></td><td height="40" align="center">' + item.creator
							+ '</td><td height="40" align="center">' + new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')
							+ '</td><td height="40" align="center">' + item.lastOperator + '</td><td height="40" align="center">'
							+ new Date(item.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')
							+ '</td><td height="40" align="center"><button type="button" class="btn btn-danger" onclick="deleteData(' + "'"
							+ item.topicId + "'" + ')">删除</button></td></tr>'
					$('.ht_cont').append(row);
				});
			} else {
				alert("查找专题出错。");
			}
		},
		error : function(msg) {
			alert(msg.result);
		}
	});
}

/**
 * 进入该专题
 */
function openTopic(topicId) {
	// 将topicId存入cookie
	setCookie("topicId", topicId);
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

/**
 * 根据页码加载数据
 * 
 * @param {整型}
 *            page 页码
 */
var search_click;
function setViewForPage(page) {

	if (search_click) {
		searchData(page);
	} else {
		allData(page);
	}
}

/**
 * 省略号点击
 */
function setPageChangeView() {
	var bt_name = parseInt($("#other").attr('name')) + 3;
	updatePageValue(bt_name);
	setViewForPage(bt_name);
	setFirstSelected();
	updateNowPage(bt_name);
}
/**
 * 更新页码数据
 * 
 * @param {Object}
 *            base_num
 */
function updatePageValue(base_num) {
	var p1 = parseInt(base_num);
	var p2 = parseInt(base_num) + 1;
	var p3 = parseInt(base_num) + 2;
	$("#p_1").val(p1);
	$("#p_2").val(p2);
	$("#p_3").val(p3);
	$("#other").attr('name', p1);
}
/**
 * 页码点击
 * 
 * @param {Object}
 *            p_id 页码
 */
function pageNumClick(p_id) {
	// background: #0e63ab;
	// color: #fff;
	var button = document.getElementById(p_id);
	var page = button.value;
	if (page != undefined && page.length > 0) {
		setViewForPage(page);
		updateNowPage(page);
		// $(this).addClass("cur").siblings().removeClass("cur");
		cleanAllSelected();
		button.style.background = '#0e63ab';
		button.style.color = '#FFFFFF';
	}
}
/**
 * 设置第一个页码按钮为选中状态
 */
function setFirstSelected() {
	cleanAllSelected();
	$("#p_1").css("background", "#0e63ab");
	$("#p_1").css("color", "#FFFFFF");
}
function setSecondSelected() {
	cleanAllSelected();
	$("#p_2").css("background", "#0e63ab");
	$("#p_2").css("color", "#FFFFFF");
}
function setThirdSelected() {
	cleanAllSelected();
	$("#p_3").css("background", "#0e63ab");
	$("#p_3").css("color", "#FFFFFF");
}
/**
 * 清除所有的选中状态
 */
function cleanAllSelected() {
	$("#p_1").css("background", "#CCCCCC");
	$("#p_1").css("color", "buttontext");
	$("#p_2").css("background", "#CCCCCC");
	$("#p_2").css("color", "buttontext");
	$("#p_3").css("background", "#CCCCCC");
	$("#p_3").css("color", "buttontext");
}
/**
 * 上一页，下一页点击
 * 
 * @param {Object}
 *            action -1上一页，1下一页
 */
function changPageOne(action) {
	var now_page = parseInt($("#down_page").attr('name'));
	var page = now_page + action;
	if (page > 0) {
		updateAllStyleAndData(page, action);
	}
}
/**
 * 跳zhuan
 */
function changePage() {
	var page = $(".go_num").val();
	if (page != undefined && page.length > 0) {
		updateAllStyleAndData(page);
	}
}
function updateAllStyleAndData(page, action) {
	updateNowPage(page);
	setViewForPage(page);
	if ((page - 1) % 3 == 0) {// 位置：第一个按钮 123 456 789
		setFirstSelected();
		if (action == 1 || action == undefined) {// 点击下一页
			updatePageValue(page);
		}
	} else if (page % 3 == 0) {// 位置：第三个按钮
		setThirdSelected();
		if (action == -1 || action == undefined) {// 点击上一页
			updatePageValue(page - 2);
		}
	} else {// 位置：第二个按钮
		setSecondSelected();
		if (action == undefined) {
			updatePageValue(page - 1);
		}
	}
}
/**
 * 更新当前页码
 * 
 * @param {Object}
 *            page 当前页
 */
function updateNowPage(page) {
	$("#down_page").attr('name', page);
}
