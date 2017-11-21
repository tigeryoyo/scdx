function addWebsite() {
	weight = $("#weight").val();
	if(weight == "" || weight==undefined)
		weight = "0";
	$.ajax({
		type : "post",
		url : "/domain/addDomain",
		data : {
			url : $("#url").val(),
			name : $("#name").val(),
			column : $("#column").val(),
            type : $("#type").val(),
			rank : $("#rank").val(),
            incidence : $("#incidence").val(),
            weight : parseInt(weight)
		},
		dataType : "json",
		success : function(msg) {
			if (msg.status == "OK") {
				jumpto("website-infor");
			} else {
				alert(msg.result);
			}
			jumpto("website-infor");
		},
		error : function() {
			 alert("您没有权限添加域名。");
		}
	})
}

function clearWebsite() {
	url: $("#url").val('');
	name: $("#name").val('');
	column: $("#column").val('');
	type: $("#type").val('');
	rank: $("#rank").val('');
	incidence: $("#incidence").val('');
	weight: $("#weight").val('');
}

function submit(fd) {
	$.ajax({
		crossDomain : true,
		url : "/domain/uploadDomainExcel",
		method : "POST",
		processData : false,
		contentType : false,
		dataType : "json",
		mimeType : "multipart/form-data",
		data : fd,
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			alert(msg.result);
			jumpto("website-infor");
		},
		complete : function() {
			stop();
			box.innerHTML="将文件拖拽到此处";
		},
		error : function() {
			 alert("您没有权限添加域名。");
		}
	});
}
