var str = "";//数据拼接
var pageSize = 10;//每页数量
var name = "",courseId = "";//姓名,课程id
var userId = "",classId="",courseId="";//用户id，班级id，课程id
var className = "";
$(function(){
    //初始化分页
    initPager(pageSize);

    //点击查询
    $(".ipnBtn").click(function(){
        initPager(pageSize);
    });

    //点击更改绑定事件
    $("tbody").on("click",".swt_but",function () {
        className = $(this).parent().parent().children().eq(0).html();
        //改样式，弹出更改框以及阴影层
        addStyle();
        //获取id
        userId = $(this).attr("data-id");
        classId = $(this).parent().parent().children().eq(0).attr("data-id");
        courseId = $("#courseId").val();
        //根据课程id查询班级名称
        findClass();
        //回显数据
        getUserDrawResult();
    })

    //点击取消
    $(".but_none").click(function(){
        $(".codeLayerbj,.addTestQu").fadeOut();
    });
    
    //点击更改保存
    $(".addDefine").click(function () {
        //获取更改班级的id
        var changeClassId = $("#mySelect").val();
        //获取演讲内容
        var content = $.trim($("#content").val());
        if (!checkContent(content)) {return;}
        $.ajax({
            url : "/backend/course/changeClass" ,
            type : "post" ,
            dataType : "json" ,
            data : {
                "changeClassId":changeClassId,
                "userId":userId,
                "classId":classId,
                "content":content,
                "courseId":courseId
            } ,
            success : function(res) {
                if ("success" == res.message) {
                    initPager(pageSize);
                    $(".codeLayerbj,.addTestQu").fadeOut();
                } else {
                    layer.msg(res.message);
                }
            }
        })
    });
});

//校验演讲内容
function checkContent(content) {
    if (content.length <= 0) {
        layer.msg("请输入演讲内容");
        return false;
    }
    if (content.length > 1000) {
        layer.msg("演讲内容不能超过1000字符");
        return false;
    }
    return true;
}

//初始化分页插件
function initPager(pageSize){
    getValue();
    $('.M-box4').pagination({
        totalData: getDrawCount(),
        showData: pageSize,
        coping: true,
        pageCount:0,
        callback: function (api) {
            var pageNo = api.getCurrent();
            findDrawByName(pageNo,pageSize,name,courseId);
        }
    },
    function () {
        findDrawByName(1,pageSize,name,courseId);
    })
}

//根枯姓名查询分班列表
function findDrawByName(pageNo,pageSize,name){
    $.ajax({
       url : "/backend/course/findDrawByName" ,
       type : "post" ,
       dataType : "json" ,
       data : {"pageNo":pageNo,"pageSize":pageSize,"name":name,"courseId":courseId} ,
       success : function(res) {
           str = "";
           $(".rev").remove();
           if ("success" == res.message) {
               appendHtml(res.data.list);
           }
           $("#draw").after(str);
       }
    });
}

//获取总数
function getDrawCount(){
    getValue();
    var count = 0;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        data : {
            "courseId":courseId,
            "name":name
        },
        url: '/backend/course/getDrawCount',
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

//获取参数
function getValue(){
    name = $.trim($("#name").val());
    courseId = $("#courseId").val();
}

//拼接数据
function appendHtml(list){
    for (var i = 0;i < list.length ; i++) {
        str += '<tr class="rev">';
        str += '<td data-id="'+list[i].classId+'">'+list[i].className+'</td>';
        str += '<td title="'+(list[i].lecturer==null?'':list[i].lecturer)+'">'+(list[i].lecturer == null ? '' : list[i].lecturer.length > 8 ? list[i].lecturer.substr(0,8)+"...":list[i].lecturer)+'</td>';
        str += '<td>'+(list[i].code == null ? '' : list[i].code)+'</td>';
        str += '<td title="'+list[i].content+'">'+(list[i].content.length > 11 ? list[i].content.substr(0,11)+"..." : list[i].content)+'</td>';
        str += '<td>'+(list[i].adaNumber == null ? '' : list[i].adaNumber)+'</td>';
        str += '<td>'+(list[i].name == null ? '' : list[i].name)+'</td>';
        str += '<td>'+(list[i].sex == null ? '' : list[i].sex)+'</td>';
        str += '<td><a href="javascript:void(0);" class="swt_but" data-sum="03" data-id="'+list[i].userId+'">更改</a></td>';
        str += '</tr>';
    }
}

//导出
function exprotDraw(){
    getValue();
    location.href = "/backend/course/exprotDraw?name="+name+"&courseId="+courseId;
}

//点击重建班级
function updateClass(){
    getValue();
    location.href = "/backend/course/updateClass?courseId="+courseId;
}

//添加样式并且显示弹出框
function addStyle(){
    var thmlH= $(window).height();
    var layerH = $(".addTestQu").height();
    $(".addTestQu").css("top",(thmlH-layerH)/2+"px");
    $(".codeLayer,.codeLayerbj").fadeIn();
}

//回填用户数据
function getUserDrawResult() {
    $.ajax({
        url : "/backend/course/getUserDrawResult" ,
        type : "post" ,
        dataType : "json" ,
        data : {"userId":userId,"classId":classId} ,
        success : function (res) {
            if ("success" == res.message) {
                $("#adaNumber").html(res.data.adaNumber);
                $("#userName").html(res.data.name);
                $("#sex").html(res.data.sex);
                $("#content").val(res.data.content);
            } else {
                layer.msg(res.message);
            }
        }
    })
}

//根据课程id查询班级名称
function findClass() {
    $.ajax({
        url : "/backend/course/findClassByCourseId" ,
        type : "post" ,
        dataType : "json" ,
        async:false,
        data : {"courseId":courseId} ,
        success : function (res) {
            str = "";
            $("#mySelect").children().remove();
            if ("success" == res.message) {
                var list = res.data;
                for (var i = 0 ;i < list.length ; i ++) {
                    if (className == list[i].name) {
                        str += '<option value="'+list[i].id+'" selected>'+list[i].name+'</option>';
                    } else {
                        str += '<option value="'+list[i].id+'">'+list[i].name+'</option>';
                    }
                }
            } else {
                layer.msg(res.message);
            }
            $("#mySelect").html(str);
        }
    })
}