var classCount = "",userCount="",courseId = "",state="1";
var str = "";
$(function () {
    //动态加载演讲内容框
    classCount = $("#hide_classCount").val();
    userCount = $("#hide_userCount").val();
    courseId = $("#courseId").val();
    //根据初始班级人数渲染的演讲内容
    appendContent(classCount);

    //判断是否是修改,是修改回显内容
    var ident = $("#ident").val();
    if (ident == 1) {
        $.ajax({
            url : "/backend/course/findContent" ,
            type : "post" ,
            dataType : "json" ,
            data : {"courseId":courseId} ,
            success : function (res) {
                if ("success" == res.message && res.data.length > 0) {
                    $(".clearfix").remove();//删除根据初始班级人数渲染的演讲内容
                    //根据最多班级人数渲染演讲内容
                    str = "";
                    appendContent(res.data.length);
                    var i = 0;
                    $(".sTexta").each(function(){
                        $(this).val(res.data[i].content);
                        i++;
                    })
                }
            }
        })
    }
});

//根据班级人数渲染的演讲内容
function appendContent(classCount) {
    for (var i = 0; i < classCount; i++) {
        str += '<div class="linH30 proCe2 clearfix">';
        str += '<span class="tiDt fontblod">演讲序号'+(i+1)+'：</span>';
        str += '<span class="TiDd">';
        str += '<textarea rows="3" maxlength="1000" placeholder="请输入演讲内容" class="sTexta siW10"></textarea>';
        str += '</span>';
        str += '</div>';
    }
    $(".squadHd").html(str);
}

//点击上一步
function lastStep(){
    location.href = "/backend/course/jump?courseId="
        + courseId + "&classCount=" + classCount+"&userCount="+userCount+"&state="+state;
}

//点击下一步
function nextStep(){
    var flag = true;
    var content = "",i=0;
    //校验信息是否填写
    $(".sTexta").each(function(){
        if ($.trim($(this).val()).length < 1) {
            layer.msg("请完整填写演讲内容");
            flag = false;
            return flag;
        } else if ($.trim($(this).val()).length > 1000) {
            layer.msg("演讲内容不能超过1000字符");
            flag = false;
            return flag;
        } else {
            content += $.trim($(this).val()) + "~,@";
        }
        i++;
    });
    if (flag) {
        $.ajax({
            url : "/backend/course/addContent" ,
            type : "post" ,
            dataType : "json" ,
            data : {"courseId":courseId,"content":content},
            success : function (res) {
                if ("success" == res.message) {
                    location.href = "/backend/course/jump?courseId="+courseId+"&state=3&ident=0";
                }
            }
        })

    }
}