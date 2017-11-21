/**
 * 权限管理
 */

function selectUserInfor(page) {
	$
		.ajax({
			type : "post",
			url : "/user/selectUserInfor",
			dataType : "json",
			data : {
				userName : $("#user_search").val(),
				start : (parseInt(10 * page - 10)),
				limit : 10
			},
			success : function(msg) {
				$('.infor_tab02 tr:not(:first)').html("");
				if (msg.status == "OK") {
					var items = msg.result;
					// userId,userName,trueName,tel,email,userRoleName,roleId
					$
						.each(
							items,
							function(idx, item) {
								row = '<tr><td width="66" height="40" align="center" bgcolor="#ffffff">'
									+ (idx + 1)
									+ '</td><td width="64" height="40" align="center" bgcolor="#ffffff">'
									+ item[1]
									+ '</td><td width="66" height="40" align="center" bgcolor="#ffffff">'
									+ item[2]
									+ '</td><td width="67" height="40" align="center" bgcolor="#ffffff">'
									+ item[5]
									+ '</td><td width="104" height="40" align="center" bgcolor="#ffffff">'
									+ item[3]
									+ '</td><td width="157" height="40" align="center" bgcolor="#ffffff">'
									+ item[4]
									+ '</td><td width="130" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick='
									+ 'accChg("'
									+ item[0]
									+ '","'
									+ item[1]
									+ '","'
									+ item[2]
									+ '","'
									+ item[3]
									+ '","'
									+ item[4]
									+ '","'
									+ item[5]
									+ '","'
									+ item[6]
									+ '")'
									+ '>编辑</button>&nbsp;&nbsp;<button type="button" class="btn btn-danger" onClick="accDel('
									+ item[0] + ')" >删除</button></td></tr>'
								$('.infor_tab02').append(row);
							});
				} else {
					alert(msg.result);
				}
			},
			error : function(msg) {
				alert("您没有权限访问该资源...");
			},
		})
}

function initUserShow(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/user/selectUserCount",
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, selectUserInfor);
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("您没有权限访问该资源...");
		}
	});
}

function initUserSearch(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/user/selectUserCount",
		dataType : "json",
		data : {
			userName : $("#user_search").val()
		},
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, selectUserInfor);
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("您没有权限访问该资源...");
		}
	});
}

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
function accChg(userId, userName, trueName, tel, email, userRoleName, roleId) {
	var acc_selectedUser = {
		"userId" : userId,
		"userName" : userName,
		"trueName" : trueName,
		"tel" : tel,
		"email" : email,
		"userRoleName" : userRoleName,
		"roleId" : roleId
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
			alert("您没有权限访问该资源...");
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
			alert("您没有权限修改用户信息。");
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
function roleChg(roleId, roleName) {
	var role_selectedRole = {
		"roleId" : roleId,
		"roleName" : roleName
	}
	setCookie("role_selectedRole", JSON.stringify(role_selectedRole));
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

function initPowerShow(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/power/selectPowerCount",
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, selectPowerInfor);
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("您没有权限访问该资源...");
		}
	});
}

function initPowerSearch(currenPage) {
	var listCount = 0;
	if ("undefined" == typeof (currenPage) || null == currenPage) {
		currenPage = 1;
	}
	$.ajax({
		type : "post",
		url : "/power/selectPowerCount",
		dataType : "json",
		date : {
			powerName : $("#power_search").val()
		},
		success : function(msg) {
			if (msg.status == "OK") {
				// alert("success");
				listCount = msg.result;
				$("#page").initPage(listCount, currenPage, selectPowerInfor);
			} else {
				alert(msg.result);
			}
		},
		error : function() {
			alert("您没有权限访问该资源...");
		}
	});
}

/**
 * 查询所有角色权限
 * 
 * @returns
 */
function selectPowerInfor(page) {
	$
		.ajax({
			type : "post",
			url : "/power/selectPowerInfor",
			dataType : "json",
			async : false,
			data : {
				powerName : $("#power_search").val(),
				start : (parseInt(10 * page - 10)),
				limit : 10
			},
			success : function(msg) {
				if (msg.status == "OK") {
					var powers = msg.result;
					$('.infor_tab02 tr:not(:first)').html("");
					$
						.each(
							powers,
							function(index, power) {
								powerId = power.powerId;
								powerName = "'" + power.powerName + "'";
								powerUrl = "'" + power.powerUrl + "'";
								row = '<tr><td width="169" height="40" align="center" bgcolor="#ffffff">'
									+ (index + 1)
									+ '</td><td width="231" height="40" align="center" bgcolor="#ffffff">'
									+ power.powerName
									+ '</td><td colspan="2" width="140" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-primary" onClick="powerChg('
									+ powerId
									+ ','
									+ powerName
									+ ','
									+ powerUrl
									+ ')">编辑</button>&nbsp;&nbsp;<button type="button" class="btn btn-danger delPower" onClick="powerDel('
									+ powerId + ')" >删除</button></td></tr>'
								$('.infor_tab02').append(row);

							});
				} else {
					alert(msg.result);
				}
			},
			error : function(msg) {
				alert("您没有权限访问该资源...");
			},
		});
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
 * 显示指定角色所拥有的权限
 */
function showRolePower() {
	var powers = selectPowerByRoleId(JSON.parse(getCookie("role_selectedRole")).roleId);
	$("#new_name_role").val(JSON.parse(getCookie("role_selectedRole")).roleName);
	$("#check_all_power").prop("checked", false);
	$('#role_power_tab').find("tbody").empty();
	$
		.each(
			powers,
			function(index, power) {
				var row = '';
				if (power.owned) {
					row = '<tr >'
						+ '<td style="text-align:left">'
						+ '<input style="width: 19px; height: 25px; padding: 0 0 5px 0;" type="checkbox" name="power" data-id="'
						+ power.powerId + '" checked="checked">&nbsp;&nbsp;' + (index + 1) + '</td>' + '<td>'
						+ power.powerName + '</td>' + '</tr>'
				} else {
					row = '<tr >'
						+ '<td style="text-align:left">'
						+ '<input style="width: 19px; height: 25px; padding: 0 0 5px 0;" type="checkbox" name="power" data-id="'
						+ power.powerId + '">&nbsp;&nbsp;' + (index + 1) + '</td>' + '<td>' + power.powerName + '</td>'
						+ '</tr>'
				}
				$('#role_power_tab').append(row);
			});
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
 * 角色管理-编辑角色-重置
 */
function resetRoleChg() {
	showRolePower();
}

/**
 * 角色管理-编辑角色-提交
 */
function submitRoleChg() {
	var array = new Array();
	var roleId = JSON.parse(getCookie("role_selectedRole")).roleId;
	$.each($("input[name='power']:checked"), function(index, power) {
		var id = parseInt($(power).attr("data-id"));
		array.push(id);
	});
	if (0 == array.length) {
		if (!confirm("您是否要删除该角色的全部权限？"))
			return false;
	}
	$.ajax({
		type : "post",
		url : "/power/changeRolePower",
		traditional : true,
		data : {
			powerIds : array,
			roleId : roleId
		},
		dataType : "json",
		async : false,
		success : function(msg) {
			alert(msg.result);
		},
		error : function(msg) {
			alert("您没有权限添加权限。");
		},
	})
	showRolePower();
}

// 角色权限编辑页面的全选
$("#check_all_power").click(function() {

	if ($("#check_all_power").prop("checked")) {
		$("input[name='power']").prop("checked", true);
	} else {
		$("input[name='power']").prop("checked", false);
	}
})

/**
 * 提交权限信息修改请求
 */
function submitPowerChg(){
	powerInfor = JSON.parse(getCookie("power_selectedPower"));
	$.ajax({
		type : "post",
		url : "/power/changePowerInfor",
		data : {
			powerId : powerInfor.powerId,
			powerName : $("#new_name_power").val(),
			powerUrl : $("#new_url_power").val()
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				alert("权限信息更新成功。");
				jumpto("auth-power");
			} else {
				alert(msg.result);
			}
		},
		error : function(msg) {
			alert("您没有权限修改权限信息。");
		},
	})
}
/**
 * 重置权限信息
 */
function resetPowerChg(){
	$("#new_name_power").val('')
	$("#new_url_power").val('')
}