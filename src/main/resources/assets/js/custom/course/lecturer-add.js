var str = "",courseId="",name="",classId= "";
var mclass = null;
$(function(){
    //查询班级
    findClassByCourseId();

    //绑定checkbox
    $("#lecturerList").on("click","i",function(){
        if ($(this).children().is(":checked")) {
            $(this).addClass("checkbox_bg_check");
            $(this).children().attr("checked");
        } else {
            $(this).removeClass("checkbox_bg_check");
            $(this).children().RemoveAttr("checked");
        }
    });

    //点击搜索讲师
    $(".ipnBtn").click(function(){
        name = $.trim($(".ipnSearch").val());
        findLecturerByClassId(classId,name,1);
    });

    //关闭弹出层
    $(".close,.but_none").click(function(){
        $(".codeLayerbj,.codeLayer").fadeOut();
    });
});

//讲师关联班级
function addTeachar(){
    var ids = "";
    $(".check").each(function(){
        if ($(this).is(":checked") && !$(this).prop("disabled")) {
            ids += $(this).attr("data-id")+",";
        }
    });
    if (ids.length < 1) {
        layer.msg("请选择讲师");
        return;
    }
    $.ajax({
        url : "/backend/course/addTeachar" ,
        type : "post" ,
        dataType : "json" ,
        data : {"ids":ids,"classId":classId,"courseId":courseId} ,
        success : function(res) {
            if ("success" == res.message) {
                $(".codeLayerbj,.codeLayer").fadeOut();
                findClassByCourseId();
            } else {
                layer.msg(res.message);
            }
        }
    })
}

//创建班级
function showClass(list) {
    for (var i = 0; i < list.length; i++) {
        str += '<tr class="rev">';
        str += '<td>' + list[i].name + '</td>';
        str += '<td>' + list[i].number + '人</td>';
        str += '<td><a href="javascript:findLecturerByClassId(\''+list[i].id+'\',null,null,this,\''+list[i].isDistribution+'\');" class="View_le swt_but" data-sum="18">'+(list[i].isDistribution == 1 ? "查看讲师" : "分配讲师")+'</a></td>';
        str += '</tr>';
    }
    if ("" != str) {
        $("#lecturer").after(str);
    }
}

//查询班级
function findClassByCourseId(){
    $.ajax({
        url : "/backend/course/findClassByCourseId" ,
        type : "post" ,
        dataType : "json" ,
        data : {"courseId":$("#courseId").val()} ,
        success : function(res) {
            $(".rev").remove();
            str = "";
            if ("success" == res.message) {
                var list = res.data;
                showClass(list);
            } else {
                layer.msg(res.message);
            }
        }
    })
}

//查看讲师
function findLecturerByClassId(id,name,state,i,isDistribution){
    if (isDistribution == 1) {
        $("#teacher_title").html("查看讲师");
    } else {
        $("#teacher_title").html("分配讲师");
    }
    mclass = $(i);
    classId = id;
    courseId = $("#courseId").val();
    $.ajax({
        url : "/backend/course/findLecturerByClassId" ,
        type : "post" ,
        dataType : "json" ,
        data : {"id":id,"courseId":courseId,"name":name} ,
        success : function (res) {
            str = "";
            if ("success" == res.message) {
                appendHtml(res.data,id);
                if (state != 1) {
                    $(".codeLayerbj,.codeLayer").fadeIn();
                }
            } else {
                layer.msg(res.message)
            }
        }
    })
}

//渲染数据
function appendHtml(list,id){
    for (var i = 0; i < list.length ; i ++) {
        str += '<li>';
        if (list[i].isDistribution == 1) {
            if (list[i].classId != id) {
                str += '<i class="input_style checkbox_bg checkbox_bg_dised" style="">';
                str += '<input type="checkbox" name="ids" class="check" value="" disabled checked data-id="'+list[i].id+'">';
            } else {
                str += '<i class="input_style checkbox_bg checkbox_bg_check">';
                str += '<input type="checkbox" name="ids" class="check" value="" checked data-id="'+list[i].id+'">';
            }
        } else {
            str += '<i class="input_style checkbox_bg">';
            str += '<input type="checkbox" name="ids" class="check" value="" data-id="'+list[i].id+'">';
        }
        str += '</i>';
        str += '<label>'+list[i].name+'</label>';
        str += '</li>';
    }
    $("#lecturerList").html(str);
}

//跳转列表页
function getDraw(){
    location.href = "/backend/course/getDraw?courseId="+$("#courseId").val();
}