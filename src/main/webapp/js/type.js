// JavaScript Document
//用户信息展示
function typeInforShow(page) {
	search_click = false;
	$
			.ajax({
				type : "post",
				url : "/sourceType/selectAllSourceType",
				data : {
					start : (parseInt(20 * page - 20)),
					limit : 20
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
						$
								.each(
										items,
										function(idx, item) {
											cookie_value1 = "'" + item.id + "'";
											cookie_value2 = "'" + item.name
													+ "'";
											row = '<tr><td width="169" height="40" align="center" bgcolor="#ffffff">'
													+'<input type="checkbox" style="width:17px;height:17px" name="typeId" value="'+item.id+'">'
													+ ((page - 1) * 20 + idx + 1)
													+ '</td><td width="231" height="40" align="center" bgcolor="#ffffff">'
													+ item.name
													+ '</td><td colspan="2" width="140" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="typeChange('
													+ cookie_value1
													+ ','
													+ cookie_value2
													+ ')" >编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delType" id="'
													+ item.id
													+ '">删除</button></td></tr>'
											$('.infor_tab02').append(row);
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
				complete : function() {
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
		url : "/sourceType/selectSourceTypeCount",
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").attr("pagelistcount",20);
				$("#page").initPage(listCount, currenPage, typeInforShow);
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
		complete : function() {
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
		url : "/sourceType/selectSourceTypeCount",
		data : {
			name : $("#type_search").val()
		},
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, typeInforSearch);
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
		complete : function() {
			stop();
		}
	})
}

function typeChange(value1, value2) {
	var json = {
		typeId : value1,
		typeName : value2
	};
	setCookie("typeInfor", JSON.stringify(json));
	jumpto("type-change");
}

// 信息搜索
function typeInforSearch(page) {
	$
			.ajax({
				type : "post",
				url : "/sourceType/selectSourceTypeByName",
				data : {
					name : $("#type_search").val(),
					start : (parseInt(20 * page - 20)),
					limit : 20
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
						var cookie_name = "'typeName'";
						$
								.each(
										items,
										function(idx, item) {
											cookie_value1 = "'" + item.id + "'";
											cookie_value2 = "'" + item.name
													+ "'";
											row = '<tr><td width="169" height="40" align="center" bgcolor="#ffffff">'
													+'<input type="checkbox" style="width:17px;height:17px" name="typeId" value="'+item.id+'">'
													+ ((page - 1) * 20 + idx + 1)
													+ '</td><td width="231" height="40" align="center" bgcolor="#ffffff">'
													+ item.name
													+ '</td><td colspan="2" width="140" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="typeChange('
													+ cookie_value1
													+ ','
													+ cookie_value2
													+ ')" >编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delType" id="'
													+ item.id
													+ '" >删除</button></td></tr>'
											$('.infor_tab02').append(row);
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
				complete : function() {
					stop();
				}
			})
}

// 用户添加
function typeInforAdd() {
	jumpto("type-add");
}
function AddtypeInfor() {
	var name = $("#addType").val();
	if (name === null || name == '') {
		alert('请输入正确信息');
		return;
	}
	$.ajax({
		type : "post",
		url : "/sourceType/insertSourceType",
		data : {
			name : name
		},
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			// console.log(msg);
			if (msg.status == "OK") {
				jumpto("type-infor");
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
            }else if(status == 500){
            	alert("请检查你输入的类型名是否已经存在！");
            }else{
            	alert(textStatus);
            }
        },
		complete : function() {
			stop();
		}
	})
}

function mergerConfirm(){
	if($('input[type=checkbox]:checked').length <2){
		alert("请至少选中两个及以上的类型！");
		return ;
	}
	$("#goodcover").css("display","block");
	$("#code").css("display","block");
	var str = "";
	$.each($('input:checkbox:checked'),function(){
        str+=$(this).parent("td").next().html()+"、";
    });
	str = str.substring(0,str.length-1);
	str+="将合并为：";
	$("#names").html(str);	
}

//类型合并
function mergerType(){	
	var ids = new Array();
	var newName = $("#newName").val();
	$.each($('input:checkbox:checked'),function(){
        ids.push(parseInt($(this).val()));
    });
	if(newName == null || newName==""){
		alert("请输入新的类型名!");
		return;
	}
	$.ajax({
		type : "post",
		url : "/sourceType/mergedSourceType",
		traditional: true,//这里设置为true
		data : {
			ids:ids,
			newName:newName
		},
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("type-infor");
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
            }else if(status == 500){
            	alert("请检查你输入的类型名是否已经存在！");
            }else{
            	alert(textStatus);
            }
        },
		complete : function() {
			stop();
			$("#goodcover").css("display","none");
			$("#code").css("display","none");
		}
	})
}


function clearType() {
	$("#addType").val('');
}

function ChangetypeInfor() {
	var type = JSON.parse(getCookie("typeInfor"));
	var name = $("#new_name_type").val().replace(' ', '');
	if (name === undefined || name == '') {
		alert('请输入正确信息');
		return;
	}
	$.ajax({
		type : "post",
		url : "/sourceType/updateSourceType",
		data : {
			name : name,
			id : parseInt(type.typeId)
		},
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert("修改成功");
				jumpto("type-infor");
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
		complete : function() {
			stop();
		}
	})
}
function clearNewtype() {
	$("#new_name_type").val('');
}

// 用户删除

$(function() {
	$(".infor_tab02").on("click", ".delType", function() {
		var typeId = $(this).attr("id");
		typeInforDel(typeId);
		function typeInforDel(typeId) {
			$.ajax({
				type : "post",
				url : "/sourceType/deleteSourceTypeById",
				data : {
					id : typeId,
				},
				dataType : "json",
				beforeSend : function() {
					begin();
				},
				success : function(msg) {
					if (msg.status == "OK") {
						jumpto("type-infor");
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
				complete : function() {
					stop();
				}
			});
		}
	})
})