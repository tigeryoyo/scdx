/**
 * 创建专题
 */
function createTopic() {
	var title = $("#chuangjian").val().replace(" ", "");
	if (title === undefined || title == '') {
		alert("请输入专题名称。");
		return;
	}

	$.ajax({
		type : "post",
		url : "/topic/create",
		data : {
			topicName : $("#chuangjian").val(),
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				jumpto("topic-list");
				$('.left ul li').eq(1).addClass('current').siblings().removeClass("current")
			} else {
				alert("创建专题失败。");
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert("网络连接错误！");
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(textStatus);
            }
        },
		complete:function(){
			stop();
		}
	});
}