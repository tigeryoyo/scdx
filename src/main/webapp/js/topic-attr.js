var editFlag = false;
var topicId = "";
function moveForword(){
	if(!editFlag)return;
	$('.topic-attr-item.selected').each(function(){
		$(this).after($(this).prev('.topic-attr-item:not(.selected)'))
	})
}

function moveBack(){
	if(!editFlag)return;
	$('.topic-attr-item.selected').each(function(){
		$(this).before($(this).next('.topic-attr-item:not(.selected)'))
	})
}

function edit(){
	$('#move-forword').css('display','block');
	$('#move-back').css('display','block');
	$('#delete').css('display','block');
	$('#save').css('display','block');
	$('#edit').css('display','none');
	editFlag = true;
}

function deleteAttr(){
	if(!editFlag)return;
	if(confirm("确定删除这些属性列？"))
		$('.topic-attr-item.selected').remove();
}

function save(){
	if(!editFlag)return;
	var ids="";
	$('.topic-attr-item').each(function(){
		ids+=$(this).attr('data-id')+";";
	})
	$
	.ajax({
		type : "post",
		url : "/topic/setTopicAttr",
		data:{
			attrIds:ids,
			topicId:topicId
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			if (msg.status == "OK") {
				$('#move-forword').css('display','none');
				$('#move-back').css('display','none');
				$('#delete').css('display','none');
				$('#save').css('display','none');
				$('#edit').css('display','block');
				editFlag = false;
				alert(msg.result);
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

function selectTopic(element){
	$('.topic-nav-item').removeClass('selected');
	$(element).addClass('selected');
	topicId = $(element).attr('data-id');
	initTopicAttr(topicId);
	if(editFlag){
		$('#move-forword').css('display','none');
		$('#move-back').css('display','none');
		$('#delete').css('display','none');
		$('#save').css('display','none');
		$('#edit').css('display','block');
		editFlag = false;
	}
}

function addAttr(element){
	if(!editFlag)return;
	var id = $(element).attr('data-id');
	var name = $(element).html();
	if($('.topic-attr-list [data-id="'+id+'"]').length>0)return;
	row = '<li data-id="'+id+'" class="topic-attr-item" onclick="chooseAttr(this)">'+name+'</li>'
	$('.topic-attr-list').append(row);
}

function chooseAttr(element){
	if(!editFlag)return;
	if($(element).hasClass('selected')){
		$(element).removeClass('selected');
	}else{
		$(element).addClass('selected');
	}
}
initGrobalAttr()
function initGrobalAttr(){
	$
	.ajax({
		type : "post",
		url : "/attr/queryAttr",
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('.grobal-attr-list').html("");
			if (msg.status == "OK") {
				var items = msg.result;
				$
					.each(
						items,
						function(idx, item) {
							row = '<li data-id="'+item.attrId+'" class="grobal-attr-item" onclick="addAttr(this)">'+item.attrMainname+'</li>'
							$('.grobal-attr-list').append(row);
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

function initTopicAttr(topicId){
	console.log(topicId)
	$
	.ajax({
		type : "post",
		url : "/topic/queryTopicAttr",
		data:{
			topicId:topicId
		},
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('.topic-attr-list').html("");
			if (msg.status == "OK") {
				var items = msg.result;
				$
					.each(
						items,
						function(idx, item) {
							row = '<li data-id="'+item.attrId+'" class="topic-attr-item" onclick="chooseAttr(this)">'+item.attrMainname+'</li>'
							$('.topic-attr-list').append(row);
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
initTopic()
function initTopic(){
	$
	.ajax({
		type : "post",
		url : "/topic/getAllTopic",
		dataType : "json",
		beforeSend : function() {
			begin();
			},
		success : function(msg) {
			$('.topic-nav').html("");
			if (msg.status == "OK") {
				var items = msg.result;
				$.each(
						items,
						function(idx, item) {
							row = '<li data-id="'+item.topicId+'" class="topic-nav-item select" onclick="selectTopic(this)" title="'+item.topicName+'">'+item.topicName+'</li>'
							$('.topic-nav').append(row);
						});
				selectTopic($('.topic-nav').children().first());
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