function addWebsite() {
	var weight = $("#weight").val();
	if(weight == "" || weight==undefined)
		weight = "0";
	var maintenanceStatus = true;
	if($("input[name='maintenance_status']:checked").val()==0){
		maintenanceStaus = false;
	}
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
            weight : parseInt(weight),
            maintenanceStatus:maintenanceStatus
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				jumpto("website-infor");
			} else {
				alert(msg.result);
			}
			jumpto("website-infor");
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
		error: function (jqXHR, textStatus, errorThrown) {
            var status = jqXHR.status;
            if(status == 0){
            	alert(textStatus);
            }else if(status == 200){
            	alert("您没有权限使用该资源...");
            }else{
            	alert(textStatus);
            }
        }		
	});
}
