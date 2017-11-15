/**
 * Created by Jack on 2017/6/2.
 */

//细节展示
function kmeans_detail() {
    $("#algorithm_detail").html("");
    var content = "k值：<input type='text' class='form-control' id='k_value' name='k_value'><br>粒度选择：<input type='radio' name='kmeans_granularity' value='1' checked='checked'> 粗粒度 <input type='radio' name='kmeans_granularity' value='2'> 细粒度";
    $("#algorithm_detail").append(content);
}

function canopy_detail() {
    $("#algorithm_detail").html("");
    var content = "阀值：<input type='text' class='form-control' style='width:60px;' id='Threshold'> <br>粒度选择：<input type='radio' name='canopy_granularity' value='1' checked='checked'> 粗粒度 <input type='radio' name='canopy_granularity' value='2'> 细粒度 ";
    $("#algorithm_detail").append(content);
}

function dbscan_detail() {
    $("#algorithm_detail").html("");
    var content = "半径：<input type='text' class='form-control' id='radius'>最小数量：<input type='text' class='form-control' id='minNum'><br>粒度选择：<input type='radio' name='dbscan_size' value='1' checked='checked'> 粗粒度 <input type='radio' name='dbscan_size' value='2'> 细粒度 ";
    $("#algorithm_detail").append(content);
}

//选择算法时，显示对应的细节
function algorithmChange(){
    var alg = $("input[name='algorithm']:checked").val();
    if(alg == 1){
        kmeans_detail();
    }else if(alg == 2){
        canopy_detail();
    }else{
        dbscan_detail();
    }

}

//
function downloadResult(){
	$(function() {
		var form = $('<form method="POST" action="/AlgorithmContainer/downloadResult">');
		$('body').append(form);
		form.submit(); // 自动提交
	});
}


//保存按钮点击事件
function conservationClick() {
    var alg = $("input[name='algorithm']:checked").val();
    if(alg == 1){
        kmeans_conservation();
    }else if(alg == 2){
        canopy_conservation();
    }else{
        dbscans_conservation();
    }
}


//聚类按钮点击事件
function clusterClick() {
    var alg = $("input[name='algorithm']:checked").val();
 //   console.log(alg+"**al");
    if(alg == 1){
        kmeans_cluster();
    }else if(alg == 2){
        canopy_cluster();
    }else{
        dbscan_cluster();
    }
}

//聚类结果处理
/**
 * 根据ajax获取的参数items生成hmtl
 * @param items 聚类结果集
 */
function showMining(items){		
	$('.summary_tab table tr:not(:first)').html('');
	for (var i = 0; i < items.length - 1; i++) {
		item = items[i];
		console.log(item)
		var rows = '<tr><td height="32" align="center">'+(i+1)+'</td><td height="32" align="center"><span>' + item[0] + '<span></td><td height="32" align="center">' + item[2] + '</td><td height="32" align="center">'+
			item[3]+
			'</td></tr>';
		$('.summary_tab table').append(rows);
	}
}

//ajax请求处理

//kmeans保存设置请求
function kmeans_conservation(){
	 var algorithm = $("input[name='algorithm']:checked").val();
     var granularity = $("input[name='kmeans_granularity']:checked").val();
    //ajax 发送请求
	 $.ajax({
	    	type:"POST",
	    	url:"/user/setAlgorithmAndGranularity",
	    	data:{
	    		algorithm:algorithm,
	    		granularity:granularity	
	    	},
	    	dataType:"json",
			
	        success : function(msg){
	        	if(msg.status=="OK"){
	        		alert("设置成功！");
					
	        	}else{
	        //		console.log(msg.result);
	        		alert(msg.result);
	        	}
	        },
			error: function(){
				alert("请求失败");
			}
	    	
	    })
}

//canopy保存设置请求
function canopy_conservation() {
	var algorithm = $("input[name='algorithm']:checked").val();
    var granularity = $("input[name='canopy_granularity']:checked").val();
   //ajax 发送请求
	 $.ajax({
	    	type:"POST",
	    	url:"/user/setAlgorithmAndGranularity",
	    	data:{
	    		algorithm:algorithm,
	    		granularity:granularity	
	    	},
	    	dataType:"json",
			
	        success : function(msg){
	        	if(msg.status=="OK"){
	        		alert("设置成功！");
					
	        	}else{
	        	//	console.log(msg.result);
	        		alert(msg.result);
	        	}
	        },
			error: function(){
				alert("请求失败");
			}
	    	
	    })
}
//dbscan保存设置请求
function dbscans_conservation() {
	var algorithm = $("input[name='algorithm']:checked").val();
    var granularity = $("input[name='dbscan_size']:checked").val();
//    console.log(algorithm+"**k");
//	 console.log(granularity+"**gr");
   //ajax 发送请求
	 $.ajax({
	    	type:"POST",
	    	url:"/user/setAlgorithmAndGranularity",
	    	data:{
	    		algorithm:algorithm,
	    		granularity:granularity	
	    	},
	    	dataType:"json",
			
	        success : function(msg){
	        	if(msg.status=="OK"){
	        		alert("设置成功！");
					
	        	}else{
	        	//	console.log(msg.result);
	        		alert(msg.result);
	        	}
	        },
			error: function(){
				alert("请求失败");
			}
	    	
	    })
}
//kmeans聚类请求
function kmeans_cluster(){
    var k_value = $("#k_value").val();
    var granularity = $("input[name='kmeans_granularity']:checked").val();
    //ajax 发送请求
    $.ajax({
        type:"POST",
        url:"/AlgorithmContainer/ClusterByKmeans",
        data:{
        	k_value:k_value,
        	granularity:granularity
        },
        dataType:"json",
        beforeSend : function(){
            begin();
        },
        success: function(msg){
            if(msg.status=="OK"){
                var items = msg.result;
                showMining(items);
            }else{
                alert(msg.result);
            }
        },
        complete : function(){
            stop();
        },
        error: function(){
            alert("请求失败!请检测k值是否为整数！");
            stop();
        }
    })
}
//canopy聚类请求
function canopy_cluster(){
    var Threshold = $("#Threshold").val();
    var granularity = $("input[name='canopy_granularity']:checked").val();
    //ajax 发送请求
    $.ajax({
        type:"post",
        url:"/AlgorithmContainer/ClusterByCanopy",
        data:{
        	Threshold:Threshold,
        	granularity:granularity

        },
        dataType:"json",
        beforeSend : function(){
            begin();
        },
        success: function(msg){
            if(msg.status=="OK"){
                showMining(msg.result);
            }else{
                alert(msg.result);
               
            }
        },
        complete : function(){
    //        console.log("all")
            stop();
        },
        error: function(){
            alert("请求失败！请确认阈值是否为纯小数！");
            stop();
        }
    })
}
//dbscan聚类请求
function dbscan_cluster(){
    var radius = $("#radius").val();
    var minNum = $("#minNum").val();
    var granularity = $("input[name='dbscan_size']:checked").val();
    //ajax 发送请求
    $.ajax({
        type:"post",
        url:"/AlgorithmContainer/ClusterByDBScan",
        data:{
        	Eps:radius,
    		MinPts:minNum,
			granularity:granularity
        },
        dataType:"json",
        beforeSend : function(){
            begin();
        },
        success: function(msg){
            if(msg.status=="OK"){
                showMining(msg.result);
            }else{
                alert(msg.result);
            }
        },
        complete : function(){
            stop();
        },
        error: function(){
            alert("请求失败！请确认半径是否为纯小数，最小数量是否为整数！");
            stop();
        }
    })
}





//文件上传拖拽功能
var fileBuf = new Array();
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
			alert("一次只能上传一份测试文件。");
			return false;
		}
		// 检测文件是不是excel文件
			var filename = fileList[0].name;
			if (filename.lastIndexOf("xls") !== -1
					|| filename.lastIndexOf("xlsx") !== -1) {
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
							addOrigfile(filename);
							$(".btn_del_all").removeAttr("disabled");
							$(".btn_upl_all").removeAttr("disabled");
							fileBuf.push(origfile);
						} else {
							alert("文件「 " + filename+" 」属行行不符合规定。");
						}
					},
					error : function() {
						stop();
					}
				});
			} else {
				alert("文件「 " + filename + " 」不是excel文件。");
			}
	}, false);
});

/**
 * 将原始文件名显示在界面
 */
function addOrigfile(filename) {
	$("#filelist").empty();
	var btn_content = '<a href="#" class="btn btn-info disabled" role="button">'
			+ filename + '</a>';
	$("#filelist").append(btn_content);
}

/**
 * 上传所有文件
 */
function uploadAll() {
		var form = new FormData();
		form.append("file", fileBuf[0]);
		$.ajax({
			async : false,
			crossDomain : true,
			url : "/AlgorithmContainer/upload",
			method : "POST",
			processData : false,
			contentType : false,
			mimeType : "multipart/form-data",
			dataType : "json",
			data : form,
			beforeSend : function(){
		          begin();
		    },
			success:function(msg) {
				if (msg.status == "OK") {
					$("#drop_area").text("文件「 " + fileBuf[0].name + " 」上传成功。");
					$(".btn_del_all").attr("disabled", true);
					$(".btn_upl_all").attr("disabled", true);
					$(".cluster").removeAttr("disabled");
				}else{
					alert(msg.result);
				}
				
			},
			error : function() {
				alert("上传失败");
				stop();
			},
			complete : function(){
	            stop();
	        },
		});
}

/**
 * 删除所有文件
 */
function deleteAll() {
	fileBuf = new Array();
	$("#drop_area").text("将文件拖拽到此处");
	$(".btn_del_all").attr("disabled", true);
	$(".btn_upl_all").attr("disabled", true);
}