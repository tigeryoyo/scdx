/**
 * Created by Jack on 2017/7/17.
 */

function websiteInforEdit() {
    $("#new_name").removeAttr("disabled");
    $("#new_column").removeAttr("disabled");
    $("#new_type").removeAttr("disabled");
    $("#new_rank").removeAttr("disabled");
    $("#new_incidence").removeAttr("disabled");
    $("#new_weight").removeAttr("disabled");
    $("#btn_edit").css("display","none");
    $("#btn_submit").css("display","block");
}

function domainOneInfoChange() {
    var uuid = getCookie("domain_id");
    $.ajax({
        type:"post",
        url:"/domain/updateDomainOne",
        data:{
            uuid:uuid,
            url:$("#url").val(),
            name:$("#new_name").val(),
            column:$("#new_column").val(),
            type:$("#new_type").val(),
            rank:$("#new_rank").val(),
            incidence:$("#new_incidence").val(),
            weight:$("#new_weight").val()
        },
        datatype:"json",
        beforeSend : function() {
			begin();
			},
        success:function (msg) {
            if(msg.status == "OK"){
                alert(msg.result);
                jumpto("website-one-details");
            }else{
            	alert(msg.result);
            }
        },
        error: function () {
        	 alert("您没有权限使用该资源...");
        },
        complete:function(){
			stop();
		}
    })
}

function domainTwoInfoChange() {
    var uuid = getCookie("domain_id");
    $.ajax({
        type:"post",
        url:"/domain/updateDomainTwo",
        data:{
            uuid:uuid,
            url:$("#url").val(),
            name:$("#new_name").val(),
            column:$("#new_column").val(),
            type:$("#new_type").val(),
            rank:$("#new_rank").val(),
            incidence:$("#new_incidence").val(),
            weight:$("#new_weight").val()
        },
        datatype:"json",
        beforeSend : function() {
			begin();
			},
        success:function (msg) {
            if(msg.status == "OK"){
                alert(msg.result);
                jumpto("website-two-details");
            }else{
            	alert(msg.result);
            }
        },
        error: function () {
        	 alert("您没有权限使用该资源...");
        },
        complete:function(){
			stop();
		}
    })
}

function back(){
	window.history.go(-1);
 //   jumpto("website-infor");
}