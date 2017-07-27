var fileArray = new Array();
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
		if (fileList.length == 0) {
			return false;
		}
		// 检测文件是不是excel文件
		for (var index = 0; index < fileList.length; index++) {
			var filename = fileList[index].name;
			if (filename.lastIndexOf("xls") !== -1 || filename.lastIndexOf("xlsx") !== -1) {
				var file = fileList[index];
				var fd = new FormData();
				fd.append("file", file);
				$.ajax({
					async : false,
					crossDomain : true,
					url : "/file/getColumnTitle",
					method : "POST",
					processData : false,
					contentType : false,
					mimeType : "multipart/form-data",
					data : fd,
					success : function(response) {
						reSetView(response, filename, fileArray.length);
					},
					error : function() {
						alert("预览失败");
						stop();
					}
				});
				fileArray.push(file);
			} else {
				alert(filename + " 不是Excel文件");
			}
		}
	}, false);
});