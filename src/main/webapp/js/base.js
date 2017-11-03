/**
 * 该js包含一些常用工具函数
 * jumpto()为所有页面跳转函数,从href=“xxx.html”--->jumpto(xxx),index.html里的页面跳转除外。
 */
bootstrapButton = $.fn.button.noConflict();
$.fn.bootstrapBtn = bootstrapButton;
function jumpto(item) {
	if (item == "index") {
		exit();
	} else {
		$.ajax({
			type : "get",
			url : item + ".html",
			async : false,
			dataType : "html",
			data : '',
			success : function(data) {
				if (data == "error") {
					window.parent.location.href = "/index.html";
				} else {
					$(".right").empty().html(data);
				}
			},
			error : function() {
				alert("jumpto出现错误。");
			}
		});
		title = document.title;
		history.pushState({
			title : title
		}, title, "?href=" + item);
	}
}

var eleMenus = $(".left a").click(function(e) {
	var e = e || window.event;
	e.preventDefault();
	if (history.pushState) {

		$(".left ul li ").removeClass("current");
		$(this).parent().addClass("current").siblings().removeClass("current"); // 点击时左侧导航项换色

		$.ajax({
			type : "get",
			url : this.href + ".html",
			async : false,
			dataType : "html",
			data : '',
			success : function(data) {
				if (data == "error") {
					window.parent.location.href = "/index.html";
				} else {
					$(".right").empty().html(data);
				}
			},
			error : function() {
				alert("jumpto出现错误。");
			}
		});
		var title = $(this).text();
		/* document.title=title; */
		if (e && /\d/.test(e.button)) {
			history.pushState({
				title : title
			}, title, "?href=" + this.href.slice(this.href.lastIndexOf('/') + 1));
		}
	}
	return false;
})

var pageTrigger = function(target) {

	var query = location.href.split('?href=')[1], eleTarget = target || null;

	if (typeof query != "undefined") {
		query = query.split('#')[0];
	}

	if (typeof query == "undefined") {
		if (eleTarget = eleMenus.get(1)) {
			history.replaceState(null, document.title, "?href=" + eleTarget.href.slice(eleTarget.href.lastIndexOf('/') + 1));
			pageTrigger(eleTarget); // 直接触发click事件

		}
	} else {
		eleMenus.each(function() {
			// 为popstate 事件准备的····
			if (eleTarget === null && this.href.slice(this.href.lastIndexOf('/') + 1) === query) {
				eleTarget = this;// popsate到之前的页面，必须要触发相应的点击事件，所以确定是那个a元素
			}
		});

		if (!eleTarget) { // 如果当前元素仍不存在,自己调用,则可能是其他的button跳转
			$.ajax({
				type : "get",
				url : query + ".html",
				async : false,
				dataType : "html",
				data : '',
				success : function(data) {
					if (data == "error") {
						window.parent.location.href = "/index.html";
					} else {
						$(".right").empty().html(data);
					}
				},
				error : function() {
					alert("jumpto出现错误。");
				}
			});

			if (event && /\d/.test(event.button)) {
				history.pushState({
					title : title
				}, title, "?href=" + query);
			}

		} else {
			$(eleTarget).trigger("click");
		}
	}
}

if (history.pushState) { // 如果浏览器支持history的pushstate属性,在前进后退的时候直接触发pageTrigger事件
	window.addEventListener("popstate", function() {
		pageTrigger();
	});
	// 默认载入
	pageTrigger();
}

/**
 * 设置cookie
 */
function setCookie(key,value){
	var Days = 1; // 此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = key + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

/**
 * 得到cookie值
 */
function getCookie(key) {
	var arr =document.cookie.match(new RegExp("(^|)"+key+"=([^;]*)(;|$)"));
	if(arr !=null) 
		return unescape(arr[2]); 
	return null;
}

/**
 * 删除cookies
 */
function delCookie(key)  
{
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval=getCookie(name);
	if(cval!=null){
		document.cookie= key + "="+cval+";expires="+exp.toGMTString();  
	}
}  

/**
 * 获取用户真实姓名
 */
function getUserName() {
	$.ajax({
		url : "/getCurrentUserTrueName",
		type : "post",
		data : "",
		success : function(msg) {
			if (msg.status == 'OK') {
				var user = msg.result;
				$(function() {
					$('.s_tit i').append(user);
				});
			} else {
				alert("获取用户信息失败。");
			}
		},
		error : function() {
			alert("获取用户信息失败...");
		}
	});
};

/**
 * 退出系统
 */
function exit() {
	$.ajax({
		url : "/logout",
		type : "post",
		data : "",
		success : function(msg) {
			window.parent.location.href = "/index.html";
		},
		error : function() {
			alert("退出请求失败。");
			window.parent.location.href = "/index.html";
		}
	});
}