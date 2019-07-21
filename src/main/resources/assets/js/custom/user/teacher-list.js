var str = "";//数据拼接
var pageSize = 10,pageNo=1;//每页数量
var teacherName = "";//姓名
var id = "",oldEmail="";//讲师id,修改之前的邮箱
var checkedLength = 0 ;
$(function(){
    //初始化分页
    initPager(pageNo,pageSize);

    //点击查询
    $(".ipnBtn").click(function(){
        initPager(pageNo,pageSize);
    });

    //绑定checkbox
    $("tbody").on("click","i",function(){
        var checkLength = $(".check").length;//获取checkbox总长度
        if ($(this).children().is(":checked")) {
            $(this).addClass("checkbox_bg_check").children().prop("checked",true);
            checkedLength ++;
        } else {
            $(this).removeClass("checkbox_bg_check").children().prop("checked",false);
            checkedLength --;
        }
        if (checkedLength == checkLength) {
            $("#selectAll").prop("checked",true);
            $("#selectAll").parent().addClass("checkbox_bg_check");
        } else {
            $("#selectAll").prop("checked",false);
            $("#selectAll").parent().removeClass("checkbox_bg_check");
        }
    });
    //全选
    $("#selectAll").click(function(){
        if ($(this).is(":checked")) {
            checkedLength = $(".check").length;
        } else {
            checkedLength = 0;
        }
    });

    //点击编辑 回填数据
    $("tbody").on("click",".swt_but",function(){
        $("#teacherTitle").html("编辑讲师");
        $(".addDefine").attr("href","javascript:confirmUpdate();");
        //根据id回填数据
        id = $(this).parent().parent().find("input").attr("data-id");
        if (id.length > 0) {
            getTeacherById(id);
        } else {
            layer.msg("暂未获取到讲师id，请稍后在试");
        }
    })
});

//确认修改讲师
function confirmUpdate() {
    //校验信息是否填写完整
    if (checkValue(oldEmail)) {
        updateTeacher(id,name,email,pwd);
    }
}

//修改讲师
function updateTeacher(id,name,email,pwd) {
    $.ajax({
        url : "/backend/user/updateTeacher" ,
        type : "post",
        dataType : "json",
        data : {"id":id,"name":name,"email":email,"pwd":pwd},
        success : function (res) {
            if ("success" == res.message) {
                layer.msg("修改成功");
                $(".codeLayer,.codeLayerbj").fadeOut();
                //初始化分页
                initPager(pageNo,pageSize);
            }
        }
    })
}


//根据id查询讲师详情
function getTeacherById(id){
    $.ajax({
        url : "/backend/user/getTeacherById" ,
        type : "post" ,
        dataType : "json" ,
        data : {"id":id} ,
        success : function (res) {
            if ("success" == res.message) {
                //回填数据
                var obj = res.data;
                $("#name").val(obj.name);
                $("#email").val(obj.email);
                oldEmail = obj.email;
                $("#pwd").val(obj.password);
                //添加样式并且显示弹出框
                addStyle();
            } else {
                layer.msg(res.message);
            }
        }
    })
}


//初始化分页插件
function initPager(pageNo,pageSize){
    getValue();
    //调用分页
    laypage.render({
        elem: 'testPager',
        count: getTeacherCount(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            findTeacherByName(obj.curr,pageSize);
        }
    });
}

//获取参数
function getValue(){
    teacherName = $.trim($("#teacherName").val());
}

//根据讲师名称查询讲师列表
function findTeacherByName(pageNo,pageSize){
    checkedLength = 0;
    $.ajax({
        url : "/backend/user/findTeacherByName" ,
        type : "post" ,
        dataType : "json" ,
        data : {"pageNo":pageNo,"pageSize":pageSize,"name":teacherName} ,
        success : function (res) {
            //清空全选按钮
            $("#selectAll").parent().removeClass("checkbox_bg_check");
            $("#selectAll").removeAttr("checked");
            str = "";
            $(".rev").remove();
            if ("success" == res.message) {
                appendHtml(res.data.list);
            }
        }
    })
}

//拼接数据
function appendHtml(list){
    if (list.length < 1) {
        $("#testPager").hide();
        $(".padTB20").show();
    } else {
        for (var i = 0; i < list.length; i++) {
            str += '<tr class="rev">';
            str += '<td><i class="input_style checkbox_bg"><input type="checkbox" name="ids" value="" class="check" data-id="' + list[i].id + '"></i>' + list[i].name + '</td>';
            str += '<td class="email">' + list[i].email + '</td>';
            str += '<td>' + list[i].password + '</td>';
            str += '<td class="mar10">';
            str += '<a href="javascript:void(0);" class="swt_but" data-sum="27">编辑</a>';
            if(list[i].deblock){
                str += '<a href="javascript:void(0);" class="deblocking" data-sum="27">解锁</a>';
            }
            str += '</td>';
            str += '</tr>';
        }
        $("#testPager").show();
        $("#teacher").after(str);
        $(".padTB20").hide();
    }
}

//解锁
$("tbody").on("click", ".deblocking", function () {
    var email = $(this).parent().parent().find(".email").html();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/backend/user/deblocking',
        data:{
            email: email
        },
        success:function(data){
            if(data.message == "success"){
                initPager(pageNo,pageSize);
                layer.msg("解锁成功",{icon: 1});
            }
        },
    });
})

//获取总数
function getTeacherCount(){
    getValue();
    var count = 0;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        data : {
            "name":teacherName
        },
        url: '/backend/user/getTeacherCount',
        async: false,
        success: function(res){
            if(res.message == "success"){
                count = res.data;
            } else {
                layer.msg(res.message)
            }
        }
    });
    return count;
}

//删除
function deleteTeacher() {
    var ids = "";
    $(".check").each(function(){
        if ($(this).is(":checked")) {
            ids += $(this).attr("data-id")+",";
        }
    });
    if (ids.length < 1) {
        layer.msg("请选择要删除的讲师");
        return;
    }
    layer.confirm("确认删除吗?",function () {
        $.ajax({
            url : "/backend/user/updateTeacherById" ,
            type : "post" ,
            dataType : "json",
            data : {"ids":ids} ,
            success : function (data) {
                if(data.code == '00'){
                    var source = $("#teacher-remove-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'teacher_remove_show' //设定一个id，防止重复弹出
                        ,resize: false
                        ,btn: ['确定']
                        ,btnAlign: 'c'
                        ,content: templateHtml
                        ,yes: function(index, layero){
                            location.reload(true);
                        }
                    });
                }else {
                    var msg = data.message?data.message:"删除老师失败！";
                    layer.msg(msg,{icon:5});
                }
            }
        });
    })
}

//显示新增弹出框
function showAddTeacher() {
    $("#teacherTitle").html("新增讲师");
    $(".addDefine").attr("href","javascript:addTeacher();");
    clearValue();
    //添加样式并且显示弹出框
    addStyle();
}

//添加样式并且显示弹出框
function addStyle(){
    var thmlH= $(window).height();
    var layerH = $(".addTestQu").height();
    $(".addTestQu").css("top",(thmlH-layerH)/2+"px");
    $(".codeLayer,.codeLayerbj").fadeIn();
}

//清空数据
function clearValue(){
    $("#name,#email,#pwd").val("");
}
