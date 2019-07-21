var userCount = "", classCount = "",state = "2";
var str = "";
var classArray = [];
var ident = 0;
var courseId = "";//课程id
$(function(){
    //判断是否是重建班级
    var clsCount = $("#classCount").val();
    if (clsCount.length > 0) {
        ident = 1;
        userCount = $("#userCount").html();
        classCount = clsCount;
        showClassNumber();
    }

    //返回上一页数据回显
    var hideClassCount = $("#hide_classCount").val();
    var hideUserCount = $("#hide_userCount").val();
    if (hideClassCount != "" && hideUserCount != "") {
        $("#userCount").html(hideUserCount);
        $("#classCount").val(hideClassCount);
        userCount = hideUserCount;
        classCount = hideClassCount;
        showClassNumber();
    }
});

//创建班级
function addClass() {
    ident = 0;
    if(!checkValue()){
        return;
    }
    userCount = $("#userCount").html();              //学员人数
    classCount = $.trim($("#classCount").val());    //每个班级的人数
    $.ajax({
        url: "/backend/course/addClass",
        type: "post",
        dataType: "json",
        data: {"userCount": userCount, "classCount": classCount, "courseId": $("#courseId").val()},
        success: function (res) {
            if ("success" == res.message) {
                showClass(userCount, classCount);
            } else {
                layer.msg(res.message);
            }
        }
    })
}

//校验字段
function checkValue() {
    var userc = $("#userCount").html();              //学员人数
    var classc = $.trim($("#classCount").val());    //每个班级的人数
    if (!isNumber(classc)) {
        layer.msg("请输入正整数");
        return false;
    }
    if ("" == classc) {
        layer.msg("请输入每班学员人数")
        return false;
    }
    if (Number(classc) < 0) {
        layer.msg("每班人数不能输入负数，请重新输入");
        return false;
    }
    if (Number(classc) == 0) {
        layer.msg("每班人数不能为0，请重新输入");
        return false;
    }
    if (Number(classc) > Number(userc)) {
        layer.msg("输入的班级人数大于已报名成功的人数，请重新输入");
        return false;
    }
    return true;
}

//创建班级
function showClass(userCount, classCount) {
    $(".rev").remove();
    str = "";
    var j = userCount;
    //获取班级个数，总人数除以每班人数
    var count = Math.ceil(userCount / classCount);
    //获取英文字母
    getEnglishLetter();
    //根据分班个数获取班级名称
    classArray = getClassArray(count);
    for (var i = 0; i < count; i++) {
        str += '<tr class="rev">';
        str += '<td>' + classArray[i] + '班</td>';
        if ((i + 1) < count) {
            j = j - classCount;
            str += '<td>' + classCount + '人</td>';
        } else {
            str += '<td>' + j + '人</td>';
        }
        str += '</tr>';
    }
    if ("" != str) {
        $("#shouClass").after(str);
        $(".btnB").html("重建班级");
        $(".squadTable,.botMarTop").show();
    }
}

function getClassArray(count) {
    var classNames = [];
    var num =  count > 26 ? count / 26 : 1;
    num = Math.ceil(num);
    var classNum = 0;
    var b = 0;
    for (var i = 0 ; i < num ; i ++) {
        for (var a = 0 ; a <= classArray.length -1; a ++,b++) {
            if (i == 0) {
                if (b < count) {
                    classNames[b] = classArray[a];
                } else {
                    break;
                }
            } else {
                if (b < count) {
                    classNames[b] =  classArray[a]+classNum;
                } else {
                    break;
                }
            }
        }
        classNum ++;
    }
    return classNames;
}

//显示演讲内容(点击下一步s)
function showContent() {
    location.href = "/backend/course/jump?courseId="
        + $("#courseId").val() + "&classCount=" + classCount+"&userCount="+userCount+"&state="+state+"&ident="+ident;
}

//获取26个英文字母
function getEnglishLetter () {
    for(var i = 0 ; i < 26 ; i++){
        classArray[i] = String.fromCharCode(65+i);
    }
}

//根据课程id查询班级名称及实际人数
function showClassNumber() {
    $.ajax({
        url: "/backend/course/findClassByCourseId",
        type: "post",
        dataType: "json",
        data: {"courseId": $("#courseId").val()},
        success: function (res) {
            str = "";
            $(".rev").remove();
            if ("success" == res.message) {
                var list = res.data;
                for (var i = 0; i < list.length ; i ++) {
                    str += '<tr class="rev">';
                    str += '<td>'+list[i].name+'</td>';
                    str += '<td>'+list[i].number+'人</td>';
                    str += '</tr>';
                }
            } else {
                layer.msg(res.message);
            }
            $("#shouClass").after(str);
            $(".btnB").html("重建班级");
            $(".squadTable,.botMarTop").show();
        }
    });
}