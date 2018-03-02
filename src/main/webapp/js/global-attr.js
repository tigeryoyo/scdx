// JavaScript Document
//用户信息展示
function attrInforShow(page) {
	$
		.ajax({
			type : "post",
			url : "/attr/queryAttr",
			data : {
				start : (parseInt(10 * page - 10)),
				limit : 10
			},
			dataType : "json",
			beforeSend : function() {
				begin();
				},
			success : function(msg) {
				$('.infor_tab02 tr:not(:first)').html("");
				if (msg.status == "OK") {
					var items = msg.result;
					var cookie_value1;
					var cookie_value2;
					var cookie_value3;
					$
						.each(
							items,
							function(idx, item) {
								cookie_value1 = "'" + item.attrId + "'";
								cookie_value2 = "'" + item.attrMainname + "'";
								cookie_value3 = "'" + item.attrAlias + "'";
								row = '<tr><td width="88" height="40" align="center" bgcolor="#ffffff">'
									+ ((page - 1) * 10 + idx + 1)
									+ '</td><td height="40" align="center" bgcolor="#ffffff">'
									+ item.attrMainname
									+ '</td><td height="40" align="center" bgcolor="#ffffff">'
									+ item.attrAlias
									+ '</td><td colspan="2" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn b btn-primary" onClick="attrChange('
									+ cookie_value1
									+ ','
									+ cookie_value2
									+ ','
									+ cookie_value3
									+ ')">编辑</button>';
								if(item.attrId>13)
									row+='<button type="button" class="btn btn-danger delAttr" style="margin-left:20px;" id="'
									+ item.attrId + '">删除</button></td></tr>';
								$('.infor_tab02').append(row);
							});
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
		})
}
function initShowPage(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/attr/queryAttrCount",
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, attrInforShow);
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
	})
}
function initSearchPage(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/attr/queryAttrCount",
		data : {
			mainName : $("#mainName").val(),
			alias : $("#alias").val(),
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, attrInforSearch);
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
	})
}
function attrChange(value1, value2, value3) {
	var json = {
		attrId : value1,
		attrMainName : value2,
		attrAlias : value3
	};
	setCookie("attrInfor", JSON.stringify(json));
	jumpto("global-attr-change");
}

// 信息搜索
function attrInforSearch(page) {
	$
		.ajax({
			type : "post",
			url : "/attr/queryAttr",
			data : {
				mainName : $("#mainName").val(),
				alias : $("#alias").val(),
				start : (parseInt(10 * page - 10)),
				limit : 10
			},
			dataType : "json",
			beforeSend : function() {
				begin();
				},
			success : function(msg) {
				$('.infor_tab02 tr:not(:first)').html("");
				if (msg.status == "OK") {
					var items = msg.result;
					var cookie_value1;
					var cookie_value2;
					var cookie_value3;
					$
						.each(
							items,
							function(idx, item) {
								cookie_value1 = "'" + item.attrId + "'";
								cookie_value2 = "'" + item.attrMainname + "'";
								cookie_value3 = "'" + item.attrAlias + "'";
								row = '<tr><td  height="40" align="center" bgcolor="#ffffff">'
									+ ((page - 1) * 10 + idx + 1)
									+ '</td><td  height="40" align="center" bgcolor="#ffffff">'
									+ item.attrMainname
									+ '</td><td  height="40" align="center" bgcolor="#ffffff">'
									+ item.attrAlias
									+ '</td><td colspan="2" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="attrChange('
									+ cookie_value1
									+ ','
									+ cookie_value2
									+ ','
									+ cookie_value3
									+ ')">编辑</button>';
									if(item.attrId>13)
										row+='<button type="button" class="btn btn-danger delAttr" style="margin-left:20px;" id="'
									+ item.attrId + '" >删除</button></td></tr>';
								$('.infor_tab02').append(row);
							});
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
		})
}

// 用户添加
function attrInforAdd() {
	jumpto("global-attr-add");
}
function addAttr() {
	$.ajax({
		type : "post",
		url : "/attr/insertAttr",
		data : {
			mainName : $("#add_mainName").val(),
			alias : $("#add_alias").val()
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				jumpto("global-attr-infor");
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
	})
}

function clearAttr() {
	$("#add_mainName").val('');
	$("#add_alias").val('');
}

function attrInforChange() {
	var attr = JSON.parse(getCookie("attrInfor"));
	$.ajax({
		type : "post",
		url : "/attr/updateAttr",
		data : {
			id : attr.attrId,
			mainName : $("#new_mainName").val(),
			alias : $("#new_alias").val()
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			console.log(msg);
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("global-attr-infor");
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
	})
}
function clearNewAttr() {
	$("#new_mainName").val('');
	$("#new_alias").val('');
}

// 用户删除
$(function() {
	$(".infor_tab02").on("click", ".delAttr", function() {
		var attrId = $(this).attr("id");
		console.log(attrId);
		if(confirm("是否删除该属性列?"))
			attrInforDel(attrId);

		function attrInforDel(attrId) {

			$.ajax({
				type : "post",
				url : "/attr/deleteAttr",
				data : {
					attrId : attrId,
				},
				dataType : "json",
				beforeSend : function() {
					begin();
					},
				success : function(msg) {
					console.log(msg);
					if (msg.status == "OK") {
						alert(msg.result);
						jumpto("global-attr-infor");
					} else {
						alert(msg.result);
					}
				},
				complete : function() {
					stop();
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
			});
		}
	})
})