/**
 * 权限管理
 */

/**
 * 用户管理-添加用户
 */
function accAdd() {
	jumpto("auth-acc_add");
}

/**
 * 用户管理-删除用户
 */
function accDel(userId) {
	$.ajax({
		type : "post",
		url : "/user/deleteUserById",
		data : {
			userId : userId,
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				alert(msg.result);
				jumpto("auth-acc");
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限删除该用户。");
		},
	});
}

/**
 * 用户管理-编辑用户
 */
function accChg(userId, userName, trueName, tel, email, userRoleName) {
	var acc_selectedUser = {
		"userId" : userId,
		"userName" : userName,
		"trueName" : trueName,
		"tel" : tel,
		"email" : email,
		"userRoleName" : userRoleName
	};
	setCookie("acc_selectedUser", JSON.stringify(acc_selectedUser));
	jumpto('auth-acc_chg');
}

/**
 * 查看所有角色
 */
function selectRole() {
	var res;
	$.ajax({
		type : "post",
		url : "/role/selectAllRole",
		dataType : "json",
		async : false,
		success : function(msg) {
			if (msg.status == "OK") {
				res = msg.result;
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限删除该用户。");
		},
	});
	// 下标0存的是当前用户的角色信息
	return res;
}

/**
 * 用户管理-添加用户-提交
 */
function submitAccAdd() {
	if (!$("#add_password").val().match(/^[\w]{6,30}$/)) {
		$("#warnPassword").html("密码必须介于6-30位之间");
		$("#add_password").focus();
		return false;
	}
	if (!$("#add_tel").val().match(/^(1[3-8][0-9]{9})$/)) {
		$("#warnTel").html("电话号码必须为11位！");
		$("#add_tel").focus();
		return false;
	}
	if (!$("#add_email").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/)) {
		$("#warnEmail").html("请输入正确的邮箱！");
		$("#add_email").focus();
		return false;
	}

	var name = $("#add_userName").val().replace(' ', '');
	if (name === undefined || name == '') {
		alert('请输入正确信息');
		return;
	}
	var trueName = $("#add_trueName").val().replace(' ', '');
	if (trueName === undefined || trueName == '') {
		alert('请输入正确信息');
		return;
	}
	var rolename = $("#add_userRoleName option:selected").val();
	if (rolename === undefined || rolename == null || rolename == 'null' || rolename == '' || rolename == '请选择角色') {
		alert('角色为空，请重新选择');
		return;
	}
	$.ajax({
		type : "post",
		url : "/user/insertUser",
		data : {
			userName : $("#add_userName").val(),
			trueName : $("#add_trueName").val(),
			password : $("#add_password").val(),
			telphone : $("#add_tel").val(),
			email : $("#add_email").val(),
			userRoleName : $("#add_userRoleName option:selected").val()
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				jumpto("auth-acc");
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限添加用户。");
		},
	})
}

/**
 * 用户管理-添加用户-重置
 */
function resetAccAdd() {
	$("#add_userName").val('');
	$("#add_trueName").val('');
	$("#add_passWord").val('');
	$("#add_tel").val('');
	$("#add_email").val('');
	$("#add_userRoleName option:selected").val('请选择角色');
}

/**
 * 用户管理-编辑用户-提交
 */
function submitAccChg() {
	if (!$("#chg_tel").val().match(/^(1[3-8][0-9]{9})$/)) {
		$("#tel_warn").html("电话号码必须为11位！");
		$("#chg_tel").focus();
		return false;
	}
	if (!$("#chg_email").val().match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/)) {
		$("#email_warn").html("请输入正确的邮箱！");
		$("#chg_email").focus();
		return false;
	}
	var rolename = $("#chg_userRoleName option:selected").val();
	if (rolename === undefined || rolename == null || rolename == 'null' || rolename == '' || rolename == '请选择角色') {
		alert('角色为空，请重新选择。');
		return;
	}
	$.ajax({
		type : "post",
		url : "/user/updateUser",
		data : {
			trueName : $("#chg_trueName").val(),
			password : $("#chg_userName").val(),
			telphone : $("#chg_tel").val(),
			email : $("#chg_email").val(),
			userRoleName : $("#chg_userRoleName option:selected").val(),
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				alert("用户信息更新成功。");
				jumpto("auth-acc");
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("修改用户信息失败。");
		},
	})
}

/**
 * 用户管理-编辑用户-重置
 */
function resetAccChg() {
	$("#chg_trueName").val('');
	$("#chg_tel").val('');
	$("#chg_email").val('');
	$("#chg_userRoleName option:selected").val('请选择角色');
}

/**
 * 角色管理-添加角色
 */
function roleAdd() {
	jumpto('auth-role_add');
}

/**
 * 角色管理-添加角色-提交
 * 
 * @returns
 */
function submitRoleAdd() {

}

/**
 * 角色管理-添加角色-重置
 */
function resetRoleAdd() {
	$(".addRole").val('');
}

/**
 * 角色管理-编辑角色
 */
function roleChg(roleId) {
	setCookie("role_selectedRoleId", roleId);
	jumpto('auth-role_chg');
}

/**
 * 根据角色查询角色权限
 * 
 * @returns
 */
function selectPowerByRoleId(roleId) {
	var res;
	$.ajax({
		type : "post",
		url : "/power/selectPowerByRoleId",
		data : {
			roleId : roleId
		},
		dataType : "json",
		async : false,
		success : function(msg) {
			if (msg.status == "OK") {
				res = msg.result;
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限编辑该用户。");
		},
	});
	return res;
}

/**
 * 查询所有角色权限
 * 
 * @returns
 */
function selectAllPower() {
	var res;
	$.ajax({
		type : "post",
		url : "/power/selectAllPower",
		dataType : "json",
		async : false,
		success : function(msg) {
			if (msg.status == "OK") {
				res = msg.result;
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限使用此资源。");
		},
	});
	return res;
}

/**
 * 资源管理-添加权限
 */
function powerAdd() {
	jumpto('auth-power_add');
}

/**
 * 资源管理-删除权限
 */
function powerDel(powerId) {
	$.ajax({
		type : "post",
		url : "/power/deletePower",
		data : {
			powerId : powerId
		},
		dataType : "json",
		async : false,
		success : function(msg) {
			alert("删除该权限成功。");
		},
		error : function(msg) {
			alert("您没有权限删除该权限。");
		},
	});
	jumpto('auth-power');
}

/**
 * 资源管理-编辑资源
 */
function powerChg(powerId, powerName, powerUrl) {
	var power_selectedPower = {
		"powerId" : powerId,
		"powerName" : powerName,
		"powerUrl" : powerUrl,
	};
	setCookie("power_selectedPower", JSON.stringify(power_selectedPower));
	jumpto('auth-power_chg');
}

/**
 * 资源管理-添加权限-提交 只有开发者（roleId=1）有权使用添加权限，故添加权限的时候自动将该权限添加至开发者权限表中
 */
function submitPowerAdd() {
	$.ajax({
		type : "post",
		url : "/power/insertPower",
		data : {
			powerName : $("#power_addPowerName").val(),
			powerUrl : $("#power_addPowerUrl").val()
		},
		dataType : "json",
		async : false,
		success : function(msg) {
			alert(msg.result);
		},
		error : function(msg) {
			alert("您没有权限添加权限。");
		},
	});
	jumpto('auth-power');
}

/**
 * 资源管理-添加权限-重置
 */
function resetPowerAdd() {
	$("#power_addPowerName").val('');
	$("#power_addPowerUrl").val('');
}