$(function () {

    var isAssignClass = $("#isAssignClass").val();
    var courseId = $("#courseId").val();
    //定义全局变量班级id
    var mClassId;
    //判断是否分班
    if(isAssignClass == "no"){
        $(".show").fadeIn();
        $(".codeLayerbj").fadeIn();
        $(".showConfirm").on("click",function () {
            $(".close").trigger("click");
        })
        $(".textCent").children("a").remove();
        return;
    }
    //初始化页面数据
    selectSpeedAndDrawList(courseId)

    //获取列表数据
    function selectSpeedAndDrawList(courseId) {
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/selectSpeedAndDrawList",
            data: {
                courseId:courseId
            },
            dataType: "json",
            success: function (data) {
                var myTemplate = Handlebars.compile($("#classDetail").html());
                $("#showDetail tr").eq(0).siblings().remove();
                $("#showDetail").append(myTemplate(data.data));

            },
            error: function () {
            }
        });
    }

    //查看或分配讲师
    $("#showDetail").on("click", ".teac", function () {
        mClassId = $(this).parents("tr").find("input").val();
        //判断是分配还是查看编辑
        var type = $(this).attr("data-sum");
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/findTeacList",
            data: {
                type:type,
                mClassId:mClassId,
                courseId:courseId
            },
            dataType: "json",
            success: function (data) {
                console.log(data);
                if(data.code == "00") {
                    $("#teacherList").children('li').remove();
                    var myTemplate = Handlebars.compile($("#showOrAddTeac").html());
                    $("#teacherList").append(myTemplate(data.data));
                    $(".teacList").fadeIn();
                    $(".codeLayerbj").fadeIn();
                    //滚动条
                    var hscroll = $(".scroll402").height();
                    if (hscroll > 400) {
                        $(".scroll402").css({"overflow-y": "scroll", "height": "400px"});
                    } else {
                        $(".scroll402").css({"overflow": "visible", "height": "auto"});
                    }
                    var thmlH = $(window).height();
                    var layerH = $(".teacList").height();
                    $(".teacList").css("top", (thmlH - layerH) / 2 + "px");
                }else {
                    layer.msg(data.message, {icon: 5});
                }
            },
            error: function () {
            }
        });
    })

    //导师搜索
    $(".ipnBtn").on("click",function () {
        var name = $(".ipnSearch").val();
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/findTeacList",
            data: {
                name:name,
                mClassId:mClassId,
                courseId:courseId
            },
            dataType: "json",
            success: function (data) {
                console.log(data);
                if(data.code == "00") {
                    $("#teacherList").children('li').remove();
                    var myTemplate = Handlebars.compile($("#showOrAddTeac").html());
                    $("#teacherList").append(myTemplate(data.data));
                    $(".teacList").fadeIn();
                    $(".codeLayerbj").fadeIn();
                    //滚动条
                    var hscroll = $(".scroll402").height();
                    if (hscroll > 400) {
                        $(".scroll402").css({"overflow-y": "scroll", "height": "400px"});
                    } else {
                        $(".scroll402").css({"overflow": "visible", "height": "auto"});
                    }
                    var thmlH = $(window).height();
                    var layerH = $(".teacList").height();
                    $(".teacList").css("top", (thmlH - layerH) / 2 + "px");
                    if(data.data.length == 0){
                        layer.msg("搜索无结果！",{icon: 1})
                    }
                }else {
                    layer.msg(data.message, {icon: 5});
                }
            },
            error: function () {
            }
        });
    })

    //选择讲师,监听checkbox
    $("#teacherList").on("click","input[type='checkbox']",function (e) {
        var checked = $(this).prop("checked");
        if(checked){
            $(this).parent("i").addClass('checkbox_bg_check');
        }else{
            $(this).parent("i").removeClass('checkbox_bg_check');
        }
    })

    //分配讲师
    $(".saveTeac").on("click", function () {
        var $teacherAll = $("#teacherList").find("li");
        var courseTeacher = [];
        $teacherAll.each(function (index, e) {
            if($(e).find("input").prop("checked")){
                var teacher = {};
                teacher.courseId = courseId;
                teacher.mClassId = mClassId;
                teacher.userId = $(e).find("input").val();
                courseTeacher.push(teacher);
            }
        })
        console.log(courseTeacher)
        if(courseTeacher.length == 0){
            layer.msg("请选择讲师!",{icon:5});
            return;
        }
        $.ajax({
            type: 'POST',
            url: "/backend/course/speeAndDraw/editOrAddTeac",
            data: {
                data: JSON.stringify(courseTeacher)
            },
            dataType: "json",
            success: function (data) {
                if(data.code == '00'){
                    layer.msg("保存成功!",{icon:1});
                    $(".close").trigger("click");
                    selectSpeedAndDrawList(courseId);
                }else {
                    layer.msg(data.message,{icon:5});
                }
            },
            error: function () {
            }
        });
    })

    //查看或创建主题
    $("#showDetail").on("click", ".Sub", function () {
        $(".scroll401").css({"overflow":"visible","height":"auto"});
        mClassId = $(this).parents("tr").find("input").val();
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/viewOrAddSub",
            data: {
                mClassId:mClassId
            },
            dataType: "json",
            success: function (data) {
                console.log(data.data);
                $("#SpeechTxDetail").children('div').remove();
                var myTemplate = Handlebars.compile($("#showOrAddSub").html());
                $("#SpeechTxDetail").append(myTemplate(data.data));
                $(".sub").fadeIn();
                $(".codeLayerbj").fadeIn();
                //滚动条
                var hscroll = $(".scroll401").height();
                if(hscroll>400){
                    $(".scroll401").css({"overflow-y":"scroll","height":"400px"});
                }else{
                    $(".scroll401").css({"overflow":"visible","height":"auto"});
                }
                var thmlH= $(window).height();
                var layerH = $(".sub").height();
                $(".sub").css("top",(thmlH-layerH)/2+"px");
            },
            error: function () {
            }
        });
    })

    //保存主题
    $(".saveSub").on("click", function () {
        var classDrawResult = [];
        var $contentAll = $("#SpeechTxDetail").find(".clearfix");
        var flag = false;
        $contentAll.each(function (index,e) {
            var content = $(e).find("textarea.siW10").val()
            if(content==null||content==undefined||content==""){
                flag = true;
            }
        })
        if(flag){
            layer.msg("还有演讲内容未填,请检查!",{icon:5});
            return;
        }
        $contentAll.each(function (index,e) {
            var classDraw = {};
            classDraw.id = $(e).find("input.classDrawId").val();
            classDraw.content = $(e).find("textarea.siW10").val();
            classDraw.code = $(e).find("input.order").val()
            classDraw.mClassId = mClassId;
            classDraw.courseId = courseId;
            classDrawResult.push(classDraw);
        })
        $.ajax({
            type: 'POST',
            url: "/backend/course/speeAndDraw/editOrAddSub",
            data: {
                data: JSON.stringify(classDrawResult)
            },
            dataType: "json",
            success: function (data) {
                console.log(data);
                if(data.code == '00'){
                    layer.msg("保存成功!",{icon:1});
                    $(".close").trigger("click");
                    selectSpeedAndDrawList(courseId);
                }else {
                    layer.msg(data.message,{icon:5});
                }
            },
            error: function () {
            }
        });
    })

    //立即抽签
    $(".drawImmediately").on("click",function () {
        $.ajax({
            type: 'POST',
            url: "/backend/course/speeAndDraw/drawImmediately",
            data: {
                courseId: courseId
            },
            dataType: "json",
            success: function (data) {
                console.log(data);
                if(data.code == '00'){
                    layer.msg("抽签成功",{icon:1});
                    //成功后跳转到抽签结果页面
                    window.location = '/backend/course/speeAndDraw/gotoDrawResult?courseId='+courseId
                }else {
                    layer.msg(data.message,{icon:5});
                }

            },
            error: function (data) {
                layer.msg("操作失败",{icon:5});
            }
        });
    })

    //判断显示条件
    Handlebars.registerHelper('compare',function(value,options){
        if(value == 0){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });

    //注册索引+1的helper
    Handlebars.registerHelper("addOne",function(index){
        return index+1;
    });
});
