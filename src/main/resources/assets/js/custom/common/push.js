function push() {
    //课程id
    var courseId = $("#courseId").val();
    //用户id
    var userIds = new Array();
    $(".check").each(function(){
        if ($(this).is(":checked")) {
            userIds.push($(this).attr("data-id"));
        }
    });
    if (userIds.length < 1) {
        layer.msg("请选择需要推送的学员");
        return;
    }
    $.ajax({
        url : "/backend/course/sendTemplateMessage" ,
        type : "post" ,
        dataType : "json" ,
        data : {"courseId":courseId,"userIds":userIds} ,
        traditional : true ,
        success : function (res) {
            var list = res.data;
            if (null == list || list.length == 0) {
                layer.msg("推送成功");
            } else {
                var msg = "";
                for (var i = 0; i < list.length ; i ++) {
                    msg += list[i] + "</br>";
                }
                layer.msg(msg);
                $(".check,#selectAll").parent().removeClass("checkbox_bg_check");
            }
        }
    });
}