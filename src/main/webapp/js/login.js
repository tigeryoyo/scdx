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
		  beforeSend : function() {
				begin();
			},
		success : function(msg) {
			if (msg.status === 'OK') {
				var Days = 1; // 此 cookie 将被保存 1 天
				var exp　= new Date();
				exp.setTime(exp.getTime() +Days*24*60*60*1000);
				document.cookie ="currentRoleId="+ escape (msg.result) + ";expires=" + exp.toGMTString();
				window.location.href = "/base.html?href=topic-list";
			} else {
				alert('用户名或密码错误。');
			}
		},
		error : function() {
			alert('登陆出现错误。');
		},
		complete:function(){
			stop();
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