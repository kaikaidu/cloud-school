var courseId,className,classNameIndex = 0,classId;//课程id,班级名称,班级字母索引
var classArray = [];//26个字母数组
var classNames = [];
var classNameIndexs = [];
var index,btnState;
var str,frStr,userId,frLength,name;//拼接数据,用户id,已添加学员的长度,学员名称
var studentLength;//学员长度
$(function(){
    courseId = $("#courseId").val();
    //查询班级
    classList();

    //获取英文字母
    getEnglishLetter();

    //点击添加班级
    $(".addClass").click(function(){
        if (!getStudentScore()) {
           layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        //班级数量不能超过学员的数量
        var length = $("#hide_count").val();
        var classLength = $(".quItem").length;
        if (length <= classLength) {
            layer.msg("添加的班级数量不能超过已报名的学员总数!");
            return;
        }
        //获取班级名称
        getClassName();
        $.ajax({
            url : "/backend/course/addClassManual" ,
            type : "post" ,
            dataType : "json" ,
            data : {"className":className,"courseId":courseId} ,
            success : function(res) {
                if ("success" == res.message) {
                    //填充数据
                    getHtml(res.data);
                    if (index < 0) {
                        $("#class").html(str);
                    } else {
                        $(".quItem:last").after(str);
                    }
                    $(".botMarTop,#classList").show();
                    $("#addClass,.fb_teg").hide();
                }
            }
        });
    });

    //点击删除
    $("#class").on("click",".deleteLink",function () {
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        var $obj = $(this).parent().parent();
        classId = $(this).attr("data-id");
        var length = $(".dm").length;
        layer.confirm('确认要删除吗？',function(index){
            if (length == 1) {
                $(".addQuItem,.textCent,.promptInfo").hide();
                location.href = "/backend/course/classSetting?sysId="+courseId+"&classState=1";
            }
            $obj.remove();
            layer.close(index);
            $.ajax({
                url : "/backend/course/deleteClass" ,
                type : "post" ,
                dataType : "json" ,
                data : {"courseId":courseId,"classId":classId} ,
                success : function (res) {
                    if ("success" == res.message) {
                        layer.msg('删除成功!', {icon: 1});
                        reset();
                    }
                }
            });
        });
    });

    //点击重新分班
    $(".hover-shadow").click(function(){
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        layer.confirm('确认重新分班吗？',function(index){
            layer.close(index);
            //删除课程下的所有班级以及演讲内容
            location.href = "/backend/course/deleteClassByCourseId?courseId="+courseId;
        });
    });

    //点击添加学员名单(展示学员列表)
    $("#class").on("click",".add_student",function () {
        //为了跟学员做关联准备
        classId = $(this).parent().next().next().find(".deleteLink").attr("data-id");
        findUser();
    });
    
    //点击搜索学员
    $(".ipnBtn").click(function () {
        btnState = 0;
        name = $.trim($(".ipnSearch").val());
        findUser();
        frLength = $(".fr_li").length;
        //为了全部学员和已选学员数据统一
        dataUnified();
    });

    //checkbox
    $("tbody").on("click","i",function () {
        userId = $(this).children().attr("data-id");
        frLength = $(".fr_li").length;
        if ($(this).children().is(":checked")) {
            $(this).addClass("checkbox_bg_check").children().prop("checked",true);
            frStr = '<li class="fr_li"><a href="#" class="fr fr_remove" data-id="'+userId+'">去除</a>"'+$(this).parent().text()+'"</li>';
            if (frLength < 1) {
                $(".list").html(frStr);
                $(".tit").html("已选择1位学员");
            } else {
                $(".fr_li:last").after(frStr);
                $(".tit").html("已选择"+(frLength+1)+"位学员");
            }
        } else {
            $(this).removeClass("checkbox_bg_check").children().prop("checked",false);
            $(".fr_remove").each(function () {
                if ($(this).attr("data-id") == userId) {
                    $(this).parent().remove();
                }
            });
            $(".tit").html("已选择"+(frLength-1)+"位学员");
        }
    });

    //点击去除-删除已添加的学员
    $(".list").on("click",".fr_remove",function () {
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        $(this).parent().remove();
        frLength = $(".fr_li").length;
        userId = $(this).attr("data-id");
        $(".checkbox_bg").each(function () {
           if ($(this).children().attr("data-id") == userId) {
               $(this).removeClass("checkbox_bg_check").children().prop("checked",false);
           }
        });
        $(".tit").html("已选择"+frLength+"位学员");
    });

    //重写关闭弹出层
    $(".close,.cancel").click(function () {
        $(".addTestQu").hide();
        clear();
    });
    
    //点击确认 学员跟班级关联,点击添加学员展示学员列表时已获取班级id
    $("#btn").click(function (event) {
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        //获取学员id
        var userIds = [];
        $(".fr_remove").each(function () {
            userIds.push($(this).attr("data-id"));
        });
        if (userIds.length < 1) {
            layer.msg("请选择学员!");
            return;
        }
        $.ajax({
            url : "/backend/course/addCourseTeacher" ,
            type : "post" ,
            dataType : "json" ,
            data : {"userIds":userIds,"classId":classId,"courseId":courseId,"studentLength":studentLength} ,
            traditional: true,
            success : function (res) {
                if ("success" == res.message) {
                    layer.msg("添加成功");
                    clear();
                    classList();
                    $(".addTestQu,.codeLayerbj").hide();
                }
            }
        });
    });

    //点击切换到自动分班
    $("#automatic").click(function(){
        if (!getStudentScore()) {
            layer.msg("演讲已开始，不能再进行操作!");
            return;
        }
        location.href = "/backend/course/classSetting?sysId="+courseId+"&classState=0";
    });
});

// 修改课程
function updateCourse() {
    location.href = "/backend/course/updateCourse?courseId="+courseId;
}

//清除
function clear() {
    name = "";
    classId = "";
    $(".ipnSearch").val("");
    $(".list").html("");
    btnState = -1;
}

//查询学员
function findUser() {
    $(".tr_remove").remove();
    $(".scroll30").attr("style","overflow:auto;height:267px;");
    $.ajax({
        url : "/backend/course/findUser" ,
        type : "post" ,
        dataType : "json" ,
        async:false,
        data : {"courseId":courseId,"name":name} ,
        success : function (res) {
            if ("success" == res.message) {
                var list = res.data;
                getHtmlForList(list);
                $(".addTestQu").each(function(){
                    $(this).fadeIn();
                    $(".codeLayerbj").fadeIn();
                    var thmlH= $(window).height();
                    var layerH = $(this).height();
                    $(this).css("top",(thmlH-layerH)/2+"px");
                });
            }
        }
    });
}

//填充学员
function getHtmlForList(list) {
    str = "";
    frStr = "";
    for (var i = 0; i < list.length ; i ++) {
        if (list[i].isAssociatedClass == 1 && classId != list[i].classId) {
            str += '<tr class="tr_remove"><td title="'+list[i].name+'"><i class="input_style checkbox_bg checkbox_bg_dised"><input type="checkbox" name="ids" disabled checked data-id="'+list[i].id+'"></i>'+(list[i].name.length > 8?list[i].name.substr(0,8)+"...":list[i].name)+'</td>';
        } else if (list[i].isAssociatedClass == 1 && classId == list[i].classId) {
            str += '<tr class="tr_remove"><td title="'+list[i].name+'"><i class="input_style checkbox_bg checkbox_bg_check"><input type="checkbox" name="ids" checked data-id="'+list[i].id+'"></i>'+(list[i].name.length > 8?list[i].name.substr(0,8)+"...":list[i].name)+'</td>';
        } else {
            str += '<tr class="tr_remove"><td title="'+list[i].name+'"><i class="input_style checkbox_bg "><input type="checkbox" name="ids" data-id="'+list[i].id+'"></i>'+(list[i].name.length > 8?list[i].name.substr(0,8)+"...":list[i].name)+'</td>';
        }
        if (classId == list[i].classId && btnState != 0) {
            frStr += '<li class="fr_li"><a href="#" class="fr fr_remove" data-id="'+list[i].id+'">去除</a>"'+list[i].name+'"</li>';
        }
        str += '<td>'+list[i].adaNumber+'</td>';
        if (list[i].sex == 0) {
            str += '<td>男</td></tr>';
        } else if (list[i].sex == 1){
            str += '<td>女</td></tr>';
        } else {
            str += '<td>夫妻</td></tr>';
        }
    }
    $("#user").after(str);
    if (btnState != 0) {
        $(".list").html(frStr);
    }
    studentLength = $(".fr_li").length;
    $(".tit").html("已选择"+studentLength+"位学员");
}

//全部学员和已选学员数据统一
function dataUnified() {
    if (frLength > 0) {
        var id;
        $(".fr_remove").each(function () {
            id = $(this).attr("data-id");
            $(".checkbox_bg").each(function () {
                if (id == $(this).children().attr("data-id")) {
                    $(this).addClass("checkbox_bg_check").children().prop("checked",true);
                }
            });
        })
    }
}

//重置班级名称
function reset() {
    var count = $(".quItem9").length;
    var num =  count > 26 ? count / 26 : 1;
    num = Math.ceil(num);
    var classNum = 0,classIndex = 0,b = 0;
    for (var i = 0 ; i < num ; i ++) {
        for (var a = 0 ; a <= classArray.length -1; a ++,b++) {
            if (i == 0) {
                if (b < count) {
                    classNames[b] = classArray[a]+"班";
                    classNameIndexs[b] = classIndex;
                } else {
                    break;
                }
            } else {
                if (b < count) {
                    classNames[b] =  classArray[a]+classNum+"班";
                    classNameIndexs[b] = classIndex;
                } else {
                    break;
                }
            }
            classIndex++;
        }
        classNum ++;
    }
    b = 0;
    $(".quItem9").find(".sInput").each(function(){
        $(this).val(classNames[b]).attr("data-index",classNameIndexs[b]);
        b++;
    });
}

//填充数据
var getHtml = function (data) {
    str = "";
    str += '<div class="quItem quItem9 clearfix">';
    str += '<div class="dt"><a href="javascript:;" class="swt_but add_student" data-sum="92">添加学员名单</a></div>';
    str += '<div class="dm"><input type="text" value="'+className+'" data-index="'+(Number(index)+1)+'" class="sInput siW10"></div>';
    str += '<div class="dd"><a href="javascript:;" class="deleteLink deleteLink02" data-id="'+data+'">删除</a></div>';
    str += '</div>';
};

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

//获取班级名称
var getClassName = function () {
    classNameIndex = $(".sInput:last").attr("data-index");
    index = classNameIndex;
    if (classNameIndex.length < 0) {
        classNameIndex = 0;
    }
    var num =  ++classNameIndex >= 26 ? (classNameIndex+1) / 26 : 1;
    num = Math.ceil(num);
    classNameIndex = classNameIndex - --num * 26;
    if (num <= 0) {
        className = classArray[classNameIndex]+"班";
    } else {
        className = classArray[classNameIndex]+num+"班";
    }
};

//获取26个英文字母
function getEnglishLetter () {
    for(var i = 0 ; i < 26 ; i++){
        classArray[i] = String.fromCharCode(65+i);
    }
}

//查询班级
function classList() {
    $.ajax({
        url : "/backend/course/classList" ,
        type : "post" ,
        dataType : "json" ,
        data : {"courseId":courseId} ,
        success : function (res) {
            if ("success" == res.message && res.data.length > 0) {
                appendhtml(res.data);
                $("#class").html(str);
                $(".botMarTop,#classList").show();
                $("#addClass,.fb_teg").hide();
            }
        }
    })
}

function appendhtml(list) {
    str = "";
    for (var i = 0; i < list.length ; i ++) {
        str += '<div class="quItem quItem9 clearfix">';
        str += '<div class="dt"><a href="javascript:;" class="swt_but add_student" data-sum="92">'+(list[i].isDistribution == 1 ? '查看编辑学员名单' : '添加学员名单')+'</a></div>';
        str += '<div class="dm"><input type="text" value="'+list[i].name+'" data-index="'+i+'" class="sInput siW10"></div>';
        str += '<div class="dd"><a href="javascript:;" class="deleteLink deleteLink02" data-id="'+list[i].id+'">删除</a></div>';
        str += '</div>';
    }
}