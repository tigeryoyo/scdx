// JavaScript Document
//用户信息展示
function weightInforShow(page){
	search_click=false;
	$.ajax({
		type:"post",
		url:"/weight/selectAllWeight",
		data:{
			start:(parseInt(10*page-10)),
			limit:10
		},
		dataType:"json",
		success: function(msg){
			$('.infor_tab02 tr:not(:first)').html("");
			if( msg.status == "OK"){
				var items = msg.result ;
				var cookie_value1;
				var cookie_value2;
				var cookie_value3;
				$.each(items,function(idx,item) {
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					cookie_value3="'"+item.weight+"'";
					row= '<tr><td width="88" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td width="162" height="40" align="center" bgcolor="#ffffff">'+item.name+'</td><td width="181" height="40" align="center" bgcolor="#ffffff">'+item.weight+'</td><td colspan="2" width="243" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn b btn-primary" onClick="setCookie('+cookie_value1+','+cookie_value2+','+cookie_value3+')">编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delWeight" id="'+item.id+'">删除</button></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        },
	})	
}
function initShowPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    $.ajax({
        type: "post",
        url: "/weight/selectWeightCount",
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,weightInforShow);
            } else {
                alert(msg.result);
            }
        },
        error: function () {
            alert("数据请求失败");
        }})
}
function initSearchPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    var obj1 = $("#stopword_search").val();
    $.ajax({
        type: "post",
        url: "/weight/selectWeightCount",
        data:{
        	name:$("#name").val(),
            weight:$("#weight").val()},
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,weightInforSearch);
            } else {
                alert(msg.result);
            }
        },
        error: function () {
            alert("数据请求失败");
        }})
}
function weightChange(value1,value2,value3){
	var json = {"weightId":value1,"weightName":value2,"weight":value3};
	setCookie("weightInfor",JSON.stringify(json));
	jumpto("weight-change");
}

// 信息搜索
function weightInforSearch(page){
	search_click=true;
	$.ajax({
		type:"post",
		url:"/weight/selectByCondition",
		data:{
			name:$("#name").val(),
			weight:$("#weight").val(),
			start:(parseInt(10*page-10)),
			limit:10
		},
		dataType:"json",
		success: function(msg){
			$('.infor_tab02 tr:not(:first)').html("");
			if( msg.status == "OK"){
				var items = msg.result ;
				var cookie_value1;
				var cookie_value2;
				var cookie_value3;
				$.each(items,function(idx,item) {
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					cookie_value3="'"+item.weight+"'";
					row= '<tr><td width="88" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td width="162" height="40" align="center" bgcolor="#ffffff">'+item.name+'</td><td width="181" height="40" align="center" bgcolor="#ffffff">'+item.weight+'</td><td colspan="2" width="243" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="weightChange('+cookie_value1+','+cookie_value2+','+cookie_value3+')">编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delWeight" id="'+item.id+'" >删除</button></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        }
	})	
}


// 用户添加
function weightInforAdd(){
	jumpto("weight-add");
}
function addWeight(){
	$.ajax({
		type:"post",
		url:"/weight/insertWeight",
		data:{
			name:$("#add_type").val(),
			weight:$("#add_weight").val()
		},
		dataType:"json",
		success: function(msg){
			if( msg.status == "OK"){
				jumpto("weight-infor");
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        },
	})	
}

function clearWeight(){
	$("#add_type").val('');
	$("#add_weight").val('');
}

function weightInforChange(){
	var weight=getCookie("weightInfor");
	$.ajax({
		type:"post",
		url:"/weight/updateWeight",
		data:{
			id:weight.weightId,
			name:$("#new_name_weight").val(),
			weight:$("#new_weight_weight").val()
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				jumpto("weight-infor");	
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        },
	})	
}
function clearNewWeight(){
	$("#new_name_weight").val('');
	$("#new_weight_weight").val('');
}


// 用户删除
$(function(){
	$(".infor_tab02").on("click",".delWeight",function(){
		var weight_id = $(this).attr("id");
		console.log(weight_id);
		weightInforDel(weight_id);
		
		function weightInforDel(weight_id){
	
			$.ajax({
				type:"post",
				url:"/weight/deleteWeight",
				data:{
					weightId:weight_id,
				} ,
				dataType:"json",
				success:function(msg){
					console.log(msg);
					if(msg.status=="OK"){
						alert(msg.result);
						jumpto("weight-infor");
					}else{
						alert(msg.result);
					}
				},
				complete : function() {
                    stop();
                },
				error: function(){
		            alert("数据请求失败");
		        },
			});
		}
	})
})