/**
 * Created by Jack on 2017/5/7.
 */
//停用词信息展示

function stopwordInforShow(page){
    $.ajax({
        type:"post",
        url:"/stopword/selectAllStopword",
        data:{
            start:(parseInt(10*page-10)),
            limit:10
        },
        dataType:"json",
        success: function(msg){
            $('.infor_tab02 tr:not(:first)').html("");
            if( msg.status == "OK"){
                var items = msg.result ;
                var cookie_value1;
                var cookie_value2;
                var cookie_value3;
                var cookie_value4;
                $.each(items,function(idx,item) {
                    cookie_value1="'"+item.id+"'";
                    cookie_value2="'"+item.word+"'";
                    cookie_value3="'"+item.creator+"'";
                    cookie_value4="'"+new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+"'";
                    row= '<tr><td colspan="1" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+item.word+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+item.creator+'</td><td colspan="1" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-danger delStopword" id="'+item.stopwordId+'">删除</button></td></tr>'
                    $('.infor_tab02').append(row);
                });
            }else{
                alert(msg.result);
                $('.infor_tab02 tr:not(:first)').html("");
            }
        },
        error: function(){
        	 alert("您没有权限使用该资源...");
        }
    })
}

function initShowPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    $.ajax({
        type: "post",
        url: "/stopword/selectStopwordCount",
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                // alert("success");
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,stopwordInforShow);
            } else {
                alert(msg.result);
            }
        },
        error: function () {
        	 alert("您没有权限使用该资源...");
        }})
    }
initShowPage(1)

// 信息搜索
function stopwordInforSearch(page){
    search_click=true;
    var obj1 = $("#stopword_search").val();
    setFirstSelected();
    $.ajax({
        type:"post",
        url:"/stopword/selectByCondition",
        data:{
            word:obj1,
            start:(parseInt(10*page-10)),
            limit:10
        },
        dataType:"json",
        success: function(msg){
            if( msg.status == "OK"){
                $('.infor_tab02 tr:not(:first)').html("");
                var items = msg.result ;
                var cookie_value1;
                var cookie_value2;
                var cookie_value3;
                var cookie_value4;
                $.each(items,function(idx,item) {
                    cookie_value1="'"+item.id+"'";
                    cookie_value2="'"+item.word+"'";
                    cookie_value3="'"+item.creator+"'";
                    cookie_value4="'"+new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+"'";
                    row= '<tr><td colspan="1" height="40" align="center" bgcolor="#ffffff">'+((page-1)*10+idx+1)+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+item.word+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+'</td><td colspan="2" height="40" align="center" bgcolor="#ffffff">'+item.creator+'</td><td colspan="1" height="40" align="center" bgcolor="#ffffff"><button type="button" class="btn btn-danger delStopword" id="'+item.stopwordId+'" >删除</button></td></tr>'
                    $('.infor_tab02').append(row);
                });
            }else{
                $('.infor_tab02 tr:not(:first)').html("");
                alert(msg.result);
            }
        },
        error: function(){
        	 alert("您没有权限使用该资源...");
        }
    })
}

function initSearchPage(currenPage){
    var listCount = 0;
    if("undefined" == typeof(currenPage) || null == currenPage){
        currenPage = 1;
    }
    var obj1 = $("#stopword_search").val();
    $.ajax({
        type: "post",
        url: "/stopword/selectStopwordCount",
        data:{word:obj1},
        dataType: "json",
        success: function (msg) {
            if (msg.status == "OK") {
                listCount = msg.result;
                $("#page").initPage(listCount,currenPage,stopwordInforSearch);
            } else {
                alert(msg.result);
            }
        },
        error: function () {
        	 alert("您没有权限使用该资源...");
        }})
    }

// 添加停用词页面跳转
function stopwordInforAdd(){
   jumpto("stopword-add");
}

// 用户删除
$(function(){
    $(".infor_tab02").on("click",".delStopword",function(){
        var stopword_id = $(this).attr("id");
            $.ajax({
                type:"post",
                url:"/stopword/deleteStopwordById",
                data:{
                    stopwordId:stopword_id
                } ,
                dataType:"json",
                success:function(msg){
                    if(msg.status=="OK"){
                    	alert(msg.result);
                        jumpto("stopword-infor");
                    }else{
                        alert(msg.result);
                    }
                } ,
                error: function(){
                	 alert("您没有权限删除停用词。");
                }
            });
    })
})
