// JavaScript Document
//用户信息展示
function websiteInforShow(page) {
    $.ajax({
        type: "post",
        url: "/domain/selectDomain",
        data: {
            start: (parseInt(10 * page - 10)),
            limit: 10
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
        	$('#domain_content').html("");
            if (msg.status == "OK") {                
                // alert("success");
                var result = msg.result;
                var one = result.one;
                var two = result.two;
                $.each(one, function (idx, item) {
                	if(item.maintenanceStatus){
	                    row = '<summary style="margin-bottom:10px;" onclick="pullDown(this)">'
	                        + '<p data-id="'+item.uuid+'">'
	                        + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">'
	                        + '<span class="glyphicon glyphicon-chevron-right" aria-label="true" style="float: left; padding: 1px 3px;"></span>'
	                        + '<input data-id="'+item.uuid+'" name="domain_one" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                        + '</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        + '<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        + '<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        + '<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
	                        + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showOneDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;color:red" onclick="changeOneStatusToUnmaintenance(this)">已维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainOne(this)">删除</a></span>' //
	                        + '</p>'
	                        + '</summary>';
                	}else{
                		row = '<summary style="margin-bottom:10px;" onclick="pullDown(this)">'
                            + '<p data-id="'+item.uuid+'">'
                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">'
	                        + '<span class="glyphicon glyphicon-chevron-right" aria-label="true" style="float: left; padding: 1px 3px;"></span>'
	                        + '<input data-id="'+item.uuid+'" name="domain_one" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                        + '</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        + '<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        + '<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        + '<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showOneDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;" onclick="changeOneStatusToMaintenance(this)">待维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainOne(this)">删除</a></span>' //
                            + '</p>'
                            + '</summary>';
                	}
                    $.each(two[idx],function(idx,item){
                    	if(item.maintenanceStatus){
	                        row +='<p data-id="'+item.uuid+'">'
	                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">----'
	                            + '<input data-id="'+item.uuid+'" name="domain_two" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
		                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                            + '</span>'
	                            + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
		                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
		                        + '<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
		                        + '<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
		                        + '<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
	                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showTwoDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;color:red" onclick="changeTwoStatusToUnmaintenance(this)">已维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainTwo(this)">删除</a></span>' //
	                            + '</p>';
                    	}else{
                    		row +='<p data-id="'+item.uuid+'">'
                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">----'
                            + '<input data-id="'+item.uuid+'" name="domain_two" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
                            + '</span>'
                            + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        + '<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        + '<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        + '<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showTwoDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;" onclick="changeTwoStatusToMaintenance(this)">待维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainTwo(this)">删除</a></span>' //
                            + '</p>';
                        }
                    })
                    
                    row = '<details style="font-size: 16px">'+row+'</details>';
                    $('#domain_content').append(row);
                });
            } else {
                alert(msg.result);
                $('#domain_content').html("");
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
    if ("undefined" == typeof(currenPage) || null == currenPage) {
        currenPage = 1;
    }
    $.ajax({
        type: "post",
        url: "/domain/selectDomainCount",
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount, currenPage, websiteInforShow);
            } else {
                //              console.log(msg.result);
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
    if ("undefined" == typeof(currenPage) || null == currenPage) {
        currenPage = 1;
    }
    $.ajax({
        type: "post",
        url: "/domain/searchDomainOneCount",
        traditional:true,
        data: {
        	url:condition.url,
            name:condition.name,
            column:condition.column,
            rank:condition.rank,
            type:condition.type,
            incidence:condition.incidence,
            weightStart:condition.weightStart,
            weightEnd:condition.weightEnd,
            isFather:condition.isFather,
            maintenance:condition.maintenance,
            timeSorting:condition.timeSorting,
            urlSorting:condition.urlSorting,
            nameSorting:condition.nameSorting,
            columnSorting:condition.columnSorting,
            rankSorting:condition.rankSorting,
            typeSorting:condition.typeSorting,
            weightSorting:condition.weightSorting,
            maintenanceSorting:condition.maintenanceSorting
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount, currenPage, websiteInforSearch);
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

// 信息搜索
function websiteInforSearch(page) {
	console.log(condition)
    $.ajax({
        type: "post",
        url: "/domain/searchDomainOne",
        traditional:true,
        data: {
            url:condition.url,
            name:condition.name,
            column:condition.column,
            rank:condition.rank,
            type:condition.type,
            incidence:condition.incidence,
            weightStart:condition.weightStart,
            weightEnd:condition.weightEnd,
            isFather:condition.isFather,
            maintenance:condition.maintenance,
            timeSorting:condition.timeSorting,
            urlSorting:condition.urlSorting,
            nameSorting:condition.nameSorting,
            columnSorting:condition.columnSorting,
            rankSorting:condition.rankSorting,
            typeSorting:condition.typeSorting,
            weightSorting:condition.weightSorting,
            maintenanceSorting:condition.maintenanceSorting,
            start: (parseInt(10 * page - 10)),
            limit: 10
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
        	$('#domain_content').html("");
            if (msg.status == "OK") {                
                // alert("success");
                var result = msg.result;
                var one = result.one;
                var two = result.two;
                $.each(one, function (idx, item) {
                	if(item.maintenanceStatus){
	                    row = '<summary style="margin-bottom:10px;" onclick="pullDown(this)">'
	                        + '<p data-id="'+item.uuid+'">'
	                        + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">'
	                        + '<span class="glyphicon glyphicon-chevron-right" aria-label="true" style="float: left; padding: 1px 3px;"></span>'
	                        + '<input data-id="'+item.uuid+'" name="domain_one" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                        + '</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        +'<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        +'<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        +'<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
	                        + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showOneDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;color:red" onclick="changeOneStatusToUnmaintenance(this)">已维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainOne(this)">删除</a></span>' //
	                        + '</p>'
	                        + '</summary>';
                	}else{
                		row = '<summary style="margin-bottom:10px;" onclick="pullDown(this)">'
                            + '<p data-id="'+item.uuid+'">'
                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">'
	                        + '<span class="glyphicon glyphicon-chevron-right" aria-label="true" style="float: left; padding: 1px 3px;"></span>'
	                        + '<input data-id="'+item.uuid+'" name="domain_one" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                        + '</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        +'<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        +'<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        +'<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showOneDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;" onclick="changeOneStatusToMaintenance(this)">待维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainOne(this)">删除</a></span>'//
                            + '</p>'
                            + '</summary>';
                	}
                    $.each(two[idx],function(idx,item){
                    	if(item.maintenanceStatus){
	                        row +='<p data-id="'+item.uuid+'">'
	                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">----'
	                            + '<input data-id="'+item.uuid+'" name="domain_two" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
		                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
	                            + '</span>'
	                            + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
		                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
		                        +'<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
		                        +'<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
		                        +'<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
	                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showTwoDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;color:red" onclick="changeTwoStatusToUnmaintenance(this)">已维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainTwo(this)">删除</a></span>'//
	                            + '</p>';
                    	}else{
                    		row +='<p data-id="'+item.uuid+'">'
                            + '<span style="width: 170px; min-height: 10px; display: inline; float: left;">----'
                            + '<input data-id="'+item.uuid+'" name="domain_two" type="checkBox" style="width:16px;height:16px;" onclick="stopBubble()">'
	                        + '<a href="http://'+item.url+'" target="_blank" title="'+item.url+'">'+item.url+'</a>'
                            + '</span>'
                            + '<span style="width: 120px;min-height: 10px; display: inline; float: left;" title="'+item.name+'">'+item.name+'</span>'
	                        + '<span style="width: 120px;min-height: 10px; display: inline; float: left" title="'+item.column+'">'+item.column+'</span>'
	                        +'<span style="width: 70px;min-height: 10px; display: inline; float: left">'+item.rank+'</span>'
	                        +'<span style="width: 60px;min-height: 10px; display: inline; float: left">'+item.type+'</span>'
	                        +'<span style="width: 50px;min-height: 10px; display: inline; float: left">'+item.weight+'</span>'
                            + '<span><a href="javascript:" style="margin: 0px 2px; text-decoration:underline" onclick="showTwoDetails(this)">详情</a><a href="javascript:" style="margin: 0px 2px; text-decoration:underline;" onclick="changeTwoStatusToMaintenance(this)">待维护</a><a href="javascript:" style="margin: 0px 2px;text-decoration:underline" onclick="delDomainTwo(this)">删除</a></span>'//
                            + '</p>';
                        }
                    })
                    row = '<details style="font-size: 16px">'+row+'</details>';
                    $('#domain_content').append(row);
                });
            } else {
                alert(msg.result);
                $('#domain_content').html("");
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
function websiteInforAdd() {
    jumpto("website-add");
}
// 用户编辑


//查看一级域名详细信息
function showOneDetails(e) {
	var uuid = $(e).parent().parent("p").attr("data-id");
    /*var minutes = 20; // 此 cookie 将被保存 20分钟
    var exp = new Date();
    exp.setTime(exp.getTime() + minutes * 60 * 1000);
    document.cookie = "domain_id" + "=" + escape(uuid) + ";expires=" + exp.toGMTString();*/
	setCookie("domain_id",uuid);
    jumpto("website-one-details");
}

//查看二级域名详细信息
function showTwoDetails(e){
	var uuid = $(e).parent().parent("p").attr("data-id");
    /*var minutes = 20; // 此 cookie 将被保存 20分钟
    var exp = new Date();
    exp.setTime(exp.getTime() + minutes * 60 * 1000);
    document.cookie = "domain_id" + "=" + escape(uuid) + ";expires=" + exp.toGMTString();*/
	setCookie("domain_id",uuid);
    jumpto("website-two-details");
}

//将已维护的一级域名状态变为待维护
function changeOneStatusToUnmaintenance(e){
	var url = $(e).parent().parent("p").find("a:first").attr("title");
	if(!confirm("是否将"+url+"的维护状态从“已维护”状态变为“待维护”？\n"))
		return ;
	var uuid = $(e).parent().parent("p").attr("data-id");
	changeOneStatus(uuid,url,false);
}
//将待维护的一级域名状态变为已维护
function changeOneStatusToMaintenance(e){
	var url = $(e).parent().parent("p").find("a:first").attr("title");
	if(!confirm("是否将"+url+"的维护状态从“待维护”状态变为“已维护”？\n"))
		return ;
	var uuid = $(e).parent().parent("p").attr("data-id");
	changeOneStatus(uuid,url,true);
}
//修改一级域名的维护状态
function changeOneStatus(uuid,url,status){
	 $.ajax({
	        type: "post",
	        url: "/domain/changeOneStatus",
	        data: {
	            uuid: uuid,
	            url:url,
	            maintenanceStatus:status
	        },
	        dataType: "json",
	        beforeSend : function() {
				begin();
				},
	        success: function (msg) {
	            if (msg.status == "OK") {
	            	initShowPage(1);
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
//将已维护的二级域名状态变为待维护
function changeTwoStatusToUnmaintenance(e){
	var url = $(e).parent().parent("p").find("a:first").attr("title");
	if(!confirm("是否将"+url+"的维护状态从“已维护”状态变为“待维护”？\n"))
		return ;
	var uuid = $(e).parent().parent("p").attr("data-id");
	changeTwoStatus(uuid,url,false);
}
//将待维护的二级域名状态变为已维护
function changeTwoStatusToMaintenance(e){
	var url = $(e).parent().parent("p").find("a:first").attr("title");
	if(!confirm("是否将"+url+"的维护状态从“待维护”状态变为“已维护”？\n"))
		return ;
	var uuid = $(e).parent().parent("p").attr("data-id");
	changeTwoStatus(uuid,url,true);
}
//修改二级域名的维护状态
function changeTwoStatus(uuid,url,status){
	$.ajax({
        type: "post",
        url: "/domain/changeTwoStatus",
        data: {
            uuid: uuid,
            url:url,
            maintenanceStatus:status
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
            	initShowPage(1)
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
//删除一级域名
function delDomainOne(e) {
	if(!confirm("该删除操作会连同二级域名一起删除！\n\n                 确定删除吗？"))
		return ;
	var uuid = $(e).parent().parent("p").attr("data-id");
    $.ajax({
        type:"post",
        url:"/domain/deleteDomainOne",
        data:{
            uuid:uuid
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                alert(msg.result);
                jumpto("website-infor");
            }else
                alert(msg.result);
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

//删除二级域名
function delDomainTwo(e) {
    var uuid = $(e).parent().parent("p").attr("data-id");
    $.ajax({
        type:"post",
        url:"/domain/deleteDomainTwo",
        data:{
            uuid:uuid
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                alert(msg.result);
                jumpto("website-infor");
            }else
                alert(msg.result);
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


function pullDown(th){
	if($(th).parent("details").attr("open")=="open"){
        $(th).parent("details").attr("open","");
        $(th).find(".glyphicon").removeClass("glyphicon-chevron-down");
        $(th).find(".glyphicon").addClass("glyphicon-chevron-right");
    }else {
        $("details").removeAttr("open");
        $(".glyphicon").removeClass("glyphicon-chevron-down");
        $(".glyphicon").addClass("glyphicon-chevron-right");
        $(th).parent("details").attr("open");
        $(th).find(".glyphicon").removeClass("glyphicon-chevron-right");
        $(th).find(".glyphicon").addClass("glyphicon-chevron-down");
    }
}


function showOp(){
	var show = $('.moreOp').css('display');
	$('.moreOp').css('display',show =='block'?'none':'block');
}

function showChoice(){
	$('.cancelChoice').css("display","inline-block");
	$('.showChoice').css('display','none');
	$('.choice').css("display","block");
}
function cancelChoice(){
	$('.showChoice').css("display","inline-block");
	$('.cancelChoice').css("display","none");
	$('.choice').css("display","none");
}

function stopBubble(e){
	window.event ? window.event.cancelBubble = true : e.stopPropagation();
}



/**
 * 批量操作部分
**/ 
function changeMaintence(element){
	if($(element).hasClass('btn-success')){
		$(element).removeClass('btn-success');
		$(element).addClass('btn-danger');
		$(element).text("已维护");
		$(element).val(1);
	}else{
		$(element).removeClass('btn-danger');
		$(element).addClass('btn-success');
		$(element).text("未维护");
		$(element).val(0);
	}
}
/**
 * 批量删除
 */
function delBatch(){
	if(!confirm("是否确定删除所选域名？其中一级域名的删除会导致其所有的二级域名删除！！！"))
		return ;
	var domain_one_id = new Array();
	$("input:checkbox[name='domain_one']:checked").each(function(){
		domain_one_id.push($(this).attr("data-id"));
	})
	var domain_two_id = new Array();
	$("input:checkbox[name='domain_two']:checked").each(function(){
		domain_two_id.push($(this).attr("data-id"));
	})
	$.ajax({
        type:"post",
        url:"/domain/deleteDomainBatch",
        traditional:true,
        data:{
            one:domain_one_id,
            two:domain_two_id
        },
        dataType: "json",
        beforeSend : function() {
			begin();
			},
        success: function (msg) {
            if (msg.status == "OK") {
                alert(msg.result);
                jumpto("website-infor");
            }else
                alert(msg.result);
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
/**
 * 批量修改
 */
function upBatch(){
	if(!confirm("是否确定修改所选域名信息？"))
		return ;
	var domain_one_id = new Array();
	$("input:checkbox[name='domain_one']:checked").each(function(){
		domain_one_id.push($(this).attr("data-id"));
	})
	var domain_two_id = new Array();
	$("input:checkbox[name='domain_two']:checked").each(function(){
		domain_two_id.push($(this).attr("data-id"));
	})
	$.ajax({
		type : "post",
		url : "/domain/updateDomainBatch",
		traditional:true,
		data : {
			one : domain_one_id,
			two : domain_two_id,
			name : $("#new_name").val(),
			column : $("#new_column").val(),
			type : $("#new_type").val(),
			rank : $("#new_rank").val(),
			incidence : $(".incidence_provience").val() + "-"
					+ $(".incidence_city").val(),
			weight : $("#new_weight").val(),
			maintenanceStatus : $("new_maintenance").val()==1
		},
		datatype : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("website-infor");
			} else {
				alert(msg.result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			alert("error")
            var status = jqXHR.status;
            if(status == 0){
            	alert(errorThrown);
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(errorThrown);
            }
        },
		complete : function() {
			stop();
		}
	})
}