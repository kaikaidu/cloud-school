var courseId,userCount, classCount,str,userId,initClassId;
var pageSize = 4,pageNo = 1;//每页数量
$(function(){
    courseId = $("#courseId").val();
    var num = $("#classCount").val();
    if (num > 0) {
        $("#createClass").html("重新创建");
    }

    //初始化分页
    initPager(pageNo,pageSize);

    //点击切换手动
    $("#manual").click(function () {
        //判断是否已开始演讲
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        location.href = "/backend/course/classSetting?sysId="+courseId+"&classState=1";
    });

    //点击创建班级
    $("#createClass").click(function () {
        //判断是否已开始演讲
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        //校验每班人数
        if(!checkValue()){
            return;
        }
        $.ajax({
            url : "/backend/course/addClass" ,
            type : "post" ,
            dataType : "json" ,
            async:false,
            data : {"userCount":userCount,"classCount":classCount,"courseId":courseId} ,
            success : function (res) {
                if ("success" == res.message) {
                    $("#createClass").html("重新创建");
                    initPager(pageNo,pageSize);
                    $(".squadTable").show();
                }
            }
        });
    });

    //点击更改
    $(".squadTable").on("click",".swt_but",function () {
        userId = $(this).attr("data-id");
        $.ajax({
            url : "/backend/course/findClassUserInfo" ,
            type : "post" ,
            dataType : "json" ,
            async: false,
            data : {"userId":userId,"courseId":courseId} ,
            success : function (res) {
                if ("success" == res.message) {
                    setHtml(res.data);
                    $(".addTestQu,.codeLayerbj").fadeIn();
                }
            }
        });
    });

    //点击更改保存
    $(".addDefine").click(function(){
        //判断是否已开始演讲
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        var classId = $("#mySelect").val();
        console.log("classId="+classId);
        console.log("userId="+userId);
        $.ajax({
           url : "/backend/course/changeClass" ,
           type : "post" ,
           dataType : "json" ,
           data : {"classId":classId,"userId":userId,"courseId":courseId,"initClassId":initClassId} ,
           success : function (res) {
               if ("success" == res.message) {
                   $(".addTestQu,.codeLayerbj").fadeOut();
                   initPager(pageNo,pageSize);
               }
           } 
        });
    });
});

//填充更改学员分班信息
function setHtml(map) {
    var obj = map.drawCuts;
    var list = map.mclassList;
    $("#adaNumber").html(obj.adaNumber);
    $("#userName").html(obj.name);
    $("#sex").html(obj.sex);
    str = "<option value='-1'>不分班</option>";
    for (var i = 0 ; i < list.length ; i ++) {
        if (obj.classId == list[i].id) {
            str += '<option value="'+list[i].id+'" selected >'+list[i].name+'</option>';
            initClassId = list[i].id;
        } else {
            str += '<option value="'+list[i].id+'" >'+list[i].name+'</option>';
        }
    }
    $("#mySelect").html(str);
}

//查询班级和学员
function initPager(pageNo,pageSize) {
    laypage.render({
        elem: 'testPager',
        count: getDrawCount(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            findDrawUser(obj.curr,pageSize);
        }
    });
}

//查询班级及学员
function findDrawUser(pageNo) {
    $.ajax({
        url : "/backend/course/findDrawUser" ,
        type : "post" ,
        dataType : "json" ,
        data : {"pageNo":pageNo,"pageSize":pageSize,"courseId":courseId} ,
        success : function(res) {
            str = "";
            $(".rev").remove();
            if ("success" == res.message && res.data.list.length > 0) {
                appendHtml(res.data.list);
                $(".squadTable").show();
            }
            $("#draw").after(str);
        }
    });
}

function appendHtml(list) {
    str = "";
    for (var i = 0; i < list.length ; i ++) {
        str += '<tr class="rev"><td>'+list[i].className+'</td>';
        str += '<td>'+list[i].adaNumber+'</td>';
        str += '<td>'+list[i].name+'</td>';
        str += '<td>'+list[i].sex+'</td>';
        str += '<td><a href="javascript:;"class="swt_but" data-sum="91" data-id="'+list[i].userId+'">编辑</a></td></tr>';
    }
}

//获取总数
function getDrawCount(){
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

//校验字段
function checkValue() {
    userCount = $("#userCount").html();              //学员人数
    classCount = $.trim($("#classCount").val());    //每个班级的人数
    if (!isNumber(classCount)) {
        layer.msg("请输入正整数");
        return false;
    }
    if ("" == classCount) {
        layer.msg("请输入每班学员人数");
        return false;
    }
    if (Number(classCount) < 0) {
        layer.msg("每班人数不能输入负数，请重新输入");
        return false;
    }
    if (Number(classCount) == 0) {
        layer.msg("每班人数不能为0，请重新输入");
        return false;
    }
    if (Number(classCount) > Number(userCount)) {
        layer.msg("输入的班级人数大于已报名成功的人数，请重新输入");
        return false;
    }
    return true;
}

//判断当前课程下是否有已评分的学员
var getStudentScore = function(){
    var flag = true;
    $.ajax({
        url : "/backend/course/getStudentScore" ,
        type : "post" ,
        dataType : "json" ,
        async : false,
        data : {"courseId":courseId} ,
        success : function (res) {
            if ("success" != res.message) {
                flag = false;
            }
        }
    });
    return flag;
};