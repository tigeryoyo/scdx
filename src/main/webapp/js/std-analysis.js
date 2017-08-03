/**
 * 拖拽
 */
$('.topicName').text("专题名称：" + getCookie("topicName"));
var fileBuf = null;
$(function() {
	// 阻止浏览器默认行。
	$(document).on({
		dragleave : function(e) { // 拖离
			e.preventDefault();
		},
		drop : function(e) { // 拖后放
			e.preventDefault();
		},
		dragenter : function(e) { // 拖进
			e.preventDefault();
		},
		dragover : function(e) { // 拖来拖去
			e.preventDefault();
		}
	});
	// 用javascript来侦听drop事件，首先要判断拖入的文件是否符合要求，包括图片类型、大小等，
	// 然后获取本地图片信息，实现预览，最后上传
	var box = document.getElementById('drop_area'); // 拖拽区域
	box.addEventListener("drop", function(e) {
		e.preventDefault(); // 取消默认浏览器拖拽效果
		var fileList = e.dataTransfer.files; // 获取文件对象
		// 检测是否是拖拽文件到页面的操作
		if (fileList.length == 0 || fileList.length > 1) {
			alert("一次只能上传一份标准数据文件。");
			return false;
		}
		// 检测文件是不是excel文件
		var filename = fileList[0].name;
		if (filename.lastIndexOf("xls") !== -1 || filename.lastIndexOf("xlsx") !== -1) {
			var origfile = fileList[0];
			var fd = new FormData();
			fd.append("origfile", origfile);
			$.ajax({
				async : false,
				crossDomain : true,
				url : "/extfile/checkExtfile",
				method : "POST",
				processData : false,
				contentType : false,
				dataType : "json",
				mimeType : "multipart/form-data",
				data : fd,
				success : function(msg) {
					if (msg.status == "OK") {
						$("#drop_area").text(filename);
						$(".btn_del_all").removeAttr("disabled");
						$(".btn_upl_all").removeAttr("disabled");
						fileBuf = origfile;
					} else {
						alert("文件「 " + filename + " 」属行行不符合规定。");
					}
				},
				error : function() {
					stop();
				}
			});
		} else {
			alert("文件[ " + filename + " ]不是excel文件。");
		}
	}, false);
});

/**
 * 上传标准数据文件
 */
function uploadStd() {
	var form = new FormData();
	form.append("stdfile", fileBuf);
	form.append("topicId", getCookie("topicId"));
	$.ajax({
		async : false,
		crossDomain : true,
		url : "/stdfile/upload",
		method : "POST",
		processData : false,
		contentType : false,
		mimeType : "multipart/form-data",
		dataType : "json",
		data : form,
		success : function(msg) {
			if (msg.status == "OK") {
				$("#drop_area").text("文件「 " + fileBuf.name + " 」上传成功。");
				$(".btn_del_all").attr("disabled", true);
				$(".btn_upl_all").attr("disabled", true);
			} else {
				alert("aa"+msg.result);
			}
		},
		error : function() {
			alert("上传失败。");
			stop();
		}
	});
}

/**
 * 删除标准数据文件
 */
function deleteStd() {
	fileBuf = null;
	$("#drop_area").text("将文件拖拽到此处");
	$(".btn_del_all").attr("disabled", true);
	$(".btn_upl_all").attr("disabled", true);
}