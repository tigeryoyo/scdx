/**
 *  账号管理js文件
 **/

/**
 * 个人信息显示
 */
$(function() {
	$.ajax({
		type : "GET",
		url : "/user/selectCurrentUser",
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				var user = msg.result;
				$("#user_name").val(user.userName);
				$("#user_trueName").val(user.trueName);
				$("#user_email").val(user.email);
				$("#user_tel").val(user.telphone);
				$("#user_createDate").val(user.createDate);
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

});

/**
 * 重置个人信息
 * 
 */
function resetAccount() {
	jumpto('acc-info');
}

/**
 * 修改个人信息
 */
function submitAccount() {
	if (!$("#user_tel").val().match(/^(1[3-8][0-9]{9})$/)) {
		$("#tel_warn").html("电话号码必须为11位！");
		$("#user_tel").focus();
		return false;
	}

	if (!$("#user_email").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/)) {
		$("#email_warn").html("请输入正确邮箱！");
		$("#user_email").focus();
		return false;
	}

	$.ajax({
		type : "post",
		url : "/user/updateUser",
		data : {
			trueName : $("#user_trueName").val(),
			telphone : $("#user_tel").val(),
			email : $("#user_email").val(),
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("acc-info");
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

/**
 * 修改密码
 */
function pwdChange() {
	var newPwd = $("#newPwd").val();
	var confirmPwd = $("#confirmPwd").val();
	if (!newPwd.match(/^[0-9a-zA-Z_#]{6,16}$/)) {
		$("#newPwd_warn").html("密码必须为6位以上字母、数字或符号!");
		$("#newPwd").focus();
		return false;
	}
	if (confirmPwd != newPwd) {
		$("#confirmPwd_warn").html("两次密码不一致!");
		$("#confirmPwd").focus();
		return false;
	}
	$.ajax({
		type : "POST",
		url : "/user/updatePassword",
		data : {
			oldPassword : $("#oldPwd").val(),
			newPassword : newPwd
		},
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				alert("密码更改成功！");
				jumpto("acc-chg_pwd");
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

/**
 * 重置密码
 * 
 */
function clearPwd() {
	$("#oldPwd").val('');
	$("#newPwd").val('');
	$("#confirmPwd").val('');
}