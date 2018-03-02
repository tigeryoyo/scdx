function addWebsite() {
	var weight = $("#weight").val();
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
            incidence : $(".incidence_provience").val() + "-"
			+ $(".incidence_city").val(),
            weight : parseInt(weight),
            maintenanceStatus:$("input[name='maintenance_status']:checked").val()==1
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
init();
function init(){
	$.ajax({
		type : "post",
		url : "/weight/selectAllWeight",
		data : {
			start : 0,
			limit : 0
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('#type').html("");
			$('#type').append('<option value="" disabled selected>请选择</option>');
			if (msg.status == "OK") {
				var items = msg.result;
				$
					.each(
						items,
						function(idx, item) {
							$('#type').append('<option>'+item.name+'</option>');
						});
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
	$.ajax({
			type : "post",
			url : "/rankWeight/selectAllWeight",
			data : {
				start : 0,
				limit : 0
			},
			dataType : "json",
			beforeSend : function() {
				begin();
				},
			success : function(msg) {
				$('#rank').html("");
				$('#rank').append('<option value="" disabled selected>请选择</option>');
				if (msg.status == "OK") {
					var items = msg.result;
					$
						.each(
							items,
							function(idx, item) {
								$('#rank').append('<option>'+item.name+'</option>');
							});
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
