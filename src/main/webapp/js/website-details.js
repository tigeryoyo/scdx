/**
 * Created by Jack on 2017/7/17.
 */

function websiteInforEdit() {
	$("#new_name").removeAttr("disabled");
	$("#new_column").removeAttr("disabled");
	$("#new_type").removeAttr("disabled");
	$("#new_rank").removeAttr("disabled");
	$(".incidence_provience").removeAttr("disabled");
	$(".incidence_city").removeAttr("disabled");
	$("#new_weight").removeAttr("disabled");
	$("input[name='maintenance_status']").removeAttr("disabled");
	$("#btn_edit").css("display", "none");
	$("#btn_back").css("margin-left", "30px");
	$("#btn_submit").css("display", "inline-block");
	$("#btn_submit_true").css("display", "inline-block");
}

function domainOneInfoChange() {
	var uuid = getCookie("domain_id");
	$.ajax({
		type : "post",
		url : "/domain/updateDomainOne",
		data : {
			uuid : uuid,
			url : $("#url").val(),
			name : $("#new_name").val(),
			column : $("#new_column").val(),
			type : $("#new_type").val(),
			rank : $("#new_rank").val(),
			incidence : $(".incidence_provience").val() + "-"
					+ $(".incidence_city").val(),
			weight : $("#new_weight").val(),
			maintenanceStatus : ($("input[name='maintenance_status']:checked")
					.val() == 1)
		},
		datatype : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("website-one-details");
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
		complete : function() {
			stop();
		}
	})
}

function domainOneInfoChangeForTrue() {
	var uuid = getCookie("domain_id");
	$.ajax({
		type : "post",
		url : "/domain/updateDomainOne",
		data : {
			uuid : uuid,
			url : $("#url").val(),
			name : $("#new_name").val(),
			column : $("#new_column").val(),
			type : $("#new_type").val(),
			rank : $("#new_rank").val(),
			incidence : $(".incidence_provience").val() + "-"
					+ $(".incidence_city").val(),
			weight : $("#new_weight").val(),
			maintenanceStatus : true
		},
		datatype : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("website-one-details");
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
		complete : function() {
			stop();
		}
	})
}

function domainTwoInfoChange() {
	var uuid = getCookie("domain_id");
	$.ajax({
		type : "post",
		url : "/domain/updateDomainTwo",
		data : {
			uuid : uuid,
			url : $("#url").val(),
			name : $("#new_name").val(),
			column : $("#new_column").val(),
			type : $("#new_type").val(),
			rank : $("#new_rank").val(),
			incidence : $(".incidence_provience").val() + "-"
					+ $(".incidence_city").val(),
			weight : $("#new_weight").val(),
			maintenanceStatus : ($("input[name='maintenance_status']:checked")
					.val() == 1)
		},
		datatype : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("website-two-details");
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
		complete : function() {
			stop();
		}
	})
}

function domainTwoInfoChangeForTrue() {
	var uuid = getCookie("domain_id");
	$.ajax({
		type : "post",
		url : "/domain/updateDomainTwo",
		data : {
			uuid : uuid,
			url : $("#url").val(),
			name : $("#new_name").val(),
			column : $("#new_column").val(),
			type : $("#new_type").val(),
			rank : $("#new_rank").val(),
			incidence : $(".incidence_provience").val() + "-"
					+ $(".incidence_city").val(),
			weight : $("#new_weight").val(),
			maintenanceStatus : true
		},
		datatype : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("website-two-details");
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
		complete : function() {
			stop();
		}
	})
}

function loadType() {
	var content = "";
	$("#new_type").empty();
	$.ajax({
		type : "post",
		url : "/sourceType/selectAllSourceType",
		data : {
			start : 0,
			limit : 0
		},
		dataType : "json",
		beforeSend : function() {
			begin();
		},
		success : function(msg) {
			if (msg.status == "OK") {
				var items = msg.result ;
				$.each(items,function(idx,item) {
					content+= '<option>'+item.name+'</option>';
				});
				$("#new_type").append(content);
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
		complete : function() {
			stop();
		}
	})
}

function back() {
	// window.history.go(-1);
	jumpto("website-infor");
}