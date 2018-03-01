/**
 * 搜索相关
 */
var condition={
		"url":"",
		"name":"",
		"column":"",
		"rank":new Array(),
		"type":new Array(),
		"incidence":"",
		"weightStart":null,
		"weightEnd":null,
		"maintenance":null,
		"isFather":null,
		"timeSorting":1,
		"urlSorting":0,
		"nameSorting":0,
		"columnSorting":0,
		"rankSorting":0,
		"typeSorting":0,
		"weightSorting":0,
		"maintenanceSorting":0,
}
function timeClick(th){
	switch(condition.timeSorting){
	case 0:
		$(th).html("时间↓");
		$(th).css("color","red");
		condition.timeSorting=1;
		break;
		case 1:
			$(th).html("时间↑");
			$(th).css("color","red");
			condition.timeSorting = 2;
			break;
		case 2:
			$(th).html("时间");
			$(th).css("color","");
			condition.timeSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function urlClick(th){
	switch(condition.urlSorting){
		case 0:
			$(th).html("域名↓");
			$(th).css("color","red");
			condition.urlSorting=1;
			break;
		case 1:
			$(th).html("域名↑");
			$(th).css("color","red");
			condition.urlSorting = 2;
			break;
		case 2:
			$(th).html("域名");
			$(th).css("color","");
			condition.urlSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function nameClick(th){
	switch(condition.nameSorting){
		case 0:
			$(th).html("名称↓");
			$(th).css("color","red");
			condition.nameSorting = 1;
			break;
		case 1:
			$(th).html("名称↑");
			$(th).css("color","red");
			condition.nameSorting = 2;
			break;
		case 2:
			$(th).html("名称");
			$(th).css("color","");
			condition.nameSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function columnClick(th){
	switch(condition.columnSorting){
		case 0:
			$(th).html("栏目↓");
			$(th).css("color","red");
			condition.columnSorting = 1;
			break;
		case 1:
			$(th).html("栏目↑");
			$(th).css("color","red");
			condition.columnSorting = 2;
			break;
		case 2:
			$(th).html("栏目");
			$(th).css("color","");
			condition.columnSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function rankClick(th){
	switch(condition.rankSorting){
		case 0:
			$(th).html("级别↓");
			$(th).css("color","red");
			condition.rankSorting = 1;
			break;
		case 1:
			$(th).html("级别↑");
			$(th).css("color","red");
			condition.rankSorting = 2;
			break;
		case 2:
			$(th).html("级别");
			$(th).css("color","");
			condition.rankSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function typeClick(th){
	switch(condition.typeSorting){
		case 0:
			$(th).html("类型↓");
			$(th).css("color","red");
			condition.typeSorting = 1;
			break;
		case 1:
			$(th).html("类型↑");
			$(th).css("color","red");
			condition.typeSorting = 2;
			break;
		case 2:
			$(th).html("类型");
			$(th).css("color","");
			condition.typeSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function weightClick(th){
	switch(condition.weightSorting){
		case 0:
			$(th).html("权重↓");
			$(th).css("color","red");
			condition.weightSorting = 1;
			break;
		case 1:
			$(th).html("权重↑");
			$(th).css("color","red");
			condition.weightSorting = 2;
			break;
		case 2:
			$(th).html("权重");
			$(th).css("color","");
			condition.weightSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}
function maintenanceClick(th){
	switch(condition.maintenanceSorting){
		case 0:
			$(th).html("维护状态↓");
			$(th).css("color","red");
			condition.maintenanceSorting = 1;
			break;
		case 1:
			$(th).html("维护状态↑");
			$(th).css("color","red");
			condition.maintenanceSorting = 2;
			break;
		case 2:
			$(th).html("维护状态");
			$(th).css("color","");
			condition.maintenanceSorting = 0;
			break;
		default:alert("error!!!");
	}
	search();
}

function choiceClick(element){
	if($(element).hasClass('normal')){
		$(element).addClass('selected');
		$(element).removeClass('normal');
	}else{
		$(element).removeClass('selected');
		$(element).addClass('normal');
	}
}


function search(){
	var url = $('#web_url').val();
	var name = $('#web_name').val();
	var column = $('#web_column').val();
	var type = new Array();
	var rank = new Array();
	var incidence = $(".incidence_provience").val() + "-"+ $(".incidence_city").val();
	var weightStart = $("#web_weight_start").val();
	var weightEnd = $("#web_weight_end").val();
	$('li.type.selected').each(function(){
		type.push($(this).text());
	})
	$('li.rank.selected').each(function(){
		rank.push($(this).text());
	})
	condition.url=url;
	condition.name=name;
	condition.column = column;
	condition.type = type;
	condition.rank = rank;
	condition.incidence = incidence;
	condition.weightStart = weightStart;
	condition.weightEnd = weightEnd;
	if($('li.maintenance.selected').length==1){
		if($('li.maintenance.selected').val()=="1"){
			condition.maintenance=true;
		}
		else
			condition.maintenance=false;
	}else{
		condition.maintenance=null;
	}
	if($('li.isFather.selected').length==1){
		if($('li.isFather.selected').val()=="1"){
			condition.isFather=true;
		}
		else
			condition.isFather=false;
	}else{
		condition.isFather=null;
	}
	console.log(condition)
	initSearchPage(1);
}










/**
 *搜索条件初始化 
**/
initTypeChoice()
function initTypeChoice(){
	$
	.ajax({
		type : "post",
		url : "/weight/selectAllWeight",
		data : {
			start : 0,
			limit : 0
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('ul.type').html("");
			$('#new_type').html("");
			if (msg.status == "OK") {
				var items = msg.result;
				$
					.each(
						items,
						function(idx, item) {
							row = '<li class="type normal" style="cursor:pointer;" value="'+item.name+'" onclick="choiceClick(this)">'+item.name+'</li>';
							$('ul.type').append(row);
							$('#new_type').append('<option>'+item.name+'</option>');
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
initRankChoice()
function initRankChoice(){
	$
	.ajax({
		type : "post",
		url : "/rankWeight/selectAllWeight",
		data : {
			start : 0,
			limit : 0
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('ul.rank').html("");
			$('#new_rank').html("");
			if (msg.status == "OK") {
				var items = msg.result;
				$
					.each(
						items,
						function(idx, item) {
							row = '<li class="rank normal" style="cursor:pointer;" value="'+item.name+'" onclick="choiceClick(this)">'+item.name+'</li>';
							$('ul.rank').append(row);
							$('#new_rank').append('<option>'+item.name+'</option>');
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