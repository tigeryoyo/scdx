// JavaScript Document
//用户信息展示
function typeInforShow(page){
	search_click=false;
	$.ajax({
		type:"post",
		url:"/sourceType/selectAllSourceType",
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
				$.each(items,function(idx,item) {
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					row= '<tr><td width="169" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td width="231" height="40" align="center" bgcolor="#ffffff">'+item.name+'</td><td colspan="2" width="140" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="setCookie('+cookie_value1+','+cookie_value2+')" >编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delType" id="'+item.id+'">删除</button></td></tr>'
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
function initShowPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    $.ajax({
        type: "post",
        url: "/sourceType/selectSourceTypeCount",
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,typeInforShow);
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
    $.ajax({
        type: "post",
        url: "/sourceType/selectSourceTypeCount",
        data:{
        	name:$("#type_search").val()},
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,typeInforSearch);
            } else {
                alert(msg.result);
            }
        },
        error: function () {
            alert("数据请求失败");
        }})
}

function setCookie(value1,value2){
	// alert(name+value);
	var cookie_name1="id";
	var cookie_name2="typeName";
	var Days = 1; // 此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = cookie_name1 +"="+ escape (value1) + ";expires=" + exp.toGMTString();
	document.cookie = cookie_name2 +"="+ escape (value2) + ";expires=" + exp.toGMTString();
	jumpto("type-change");
}

// 信息搜索
function typeInforSearch(page){
	$.ajax({
		type:"post",
		url:"/sourceType/selectSourceTypeByName",
		data:{
			name:$("#type_search").val(),
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
				var cookie_name="'typeName'";
				$.each(items,function(idx,item) {
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					row= '<tr><td width="169" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td width="231" height="40" align="center" bgcolor="#ffffff">'+item.name+'</td><td colspan="2" width="140" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="setCookie('+cookie_value1+','+cookie_value2+')" >编辑</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" class="btn btn-danger delType" id="'+item.id+'" >删除</button></td></tr>'
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


// 用户添加
function typeInforAdd() {
	jumpto("type-add");
}
function AddtypeInfor(){
	var submit=$("#addType").val().replace(' ','');
	if(submit===undefined||submit==''){
	    alert('请输入正确信息');
	    return;
	}
	$.ajax({
		type:"post",
		url:"/sourceType/insertSourceType",
		data:{
			name:submit
		},
		dataType:"json",
		success: function(msg){
//			console.log(msg);
			if( msg.status == "OK"){
				jumpto("type-infor");
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        }
	})	
}


function clearType(){
	$("#addType").val('');
}


function ChangetypeInfor(){
	var newId=getCookie("id");
	var name = $("#new_name_type").val().replace(' ','');
	if(name===undefined||name==''){
	    alert('请输入正确信息');
	    return;
	}
	$.ajax({
		type:"post",
		url:"/sourceType/updateSourceType",
		data:{
			name:name,
			id:newId
		},
		dataType:"json",
		success: function(msg){
			if( msg.status == "OK"){
				alert("修改成功");	
				jumpto("type-infor");
			}else{
				alert(msg.result);
			}
		},
		error: function(){
            alert("数据请求失败");
        }
	})	
}
function clearNewtype(){
	$("#new_name_type").val('');
}

// 用户删除

$(function(){
	$(".infor_tab02").on("click",".delType",function(){
		var typeId = $(this).attr("id");
		typeInforDel(typeId);
		function typeInforDel(typeId){
			$.ajax({
				type:"post",
				url:"/sourceType/deleteSourceTypeById",
				data:{
					id:typeId,
				} ,
				dataType:"json",
				success:function(msg){
					if(msg.status=="OK"){
						jumpto("type-infor");
					}else{
						alert(msg.result);
					}
				} ,
				error: function(){
		            alert("数据请求失败");
		        },
			});
		}
	})
})