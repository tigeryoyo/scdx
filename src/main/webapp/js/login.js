/**
 * 登陆验证
 */
function login() {
	var username = $("#form-username").val().replace(' ', '');
	var password = $("#form-password").val().replace(' ', '');
	if (username === undefined || username === '' || password === undefined || password === '') {
		alert('请输入用户名和密码');
		return;
	}
	$.ajax({
		url : '/login',
		type : 'post',
		data : {
			username : username,
			password : password
		},
		success : function(msg) {
			if (msg.status === 'OK') {
				window.location.href = "/base.html?href=topic-list";
			} else {
				alert('用户名和密码错误');
			}
		},
		error : function() {
			alert('数据请求失败');
		}
	});
}

/**
 * 回车响应登陆
 */
function keyDown(e) {
	var ev = window.event || e;
	if (ev.keyCode == 13) {
		login();
	}
}