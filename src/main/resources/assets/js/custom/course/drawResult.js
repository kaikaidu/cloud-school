$(function () {
    var courseId = $("#courseId").val();
    var pageSize = 10;


    initPager(pageSize);

    /**
     * 初始化分页插件
     * @param pageSize
     */
    function initPager(pageSize){
        //调用分页
        laypage.render({
            elem: 'drawResultPager',
            count: getTotalData(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj){
                showDrawResult(obj.curr,pageSize);
            }
        });
    }

    //查看抽签结果
    function showDrawResult(pageNo, pageSize) {
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/selectDrawResult",
            data: {
                pageNo : pageNo,
                pageSize : pageSize,
                courseId : courseId
            },
            dataType: "json",
            success: function (data) {
                var myTemplate = Handlebars.compile($("#drawResult").html());
                $("tbody tr").eq(0).siblings().remove();
                $("tbody").append(myTemplate(data.data));
            },
            error: function (data) {
                layer.msg("操作失败",{icon:5});
            }
        });
    }

    //查询数量
    function getTotalData() {
        var count = 0;
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/selectDrawResultCount",
            data: {
                courseId : courseId
            },
            dataType: "json",
            async: false,
            success: function (data) {
                if(data.message == "success"){
                    count = data.data;
                }
            },
            error: function (data) {
                layer.msg("操作失败",{icon:5});
            }
        });
        return count;
    }

    //查看演讲主题
    $("tbody").on("click","#showSub",function () {
        var classDrawId = $(this).parent().parent().find("input").val();
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/selectSub",
            data: {
                classDrawId: classDrawId
            },
            dataType: "json",
            success: function (data) {
                $("#showSubDetail").children("div").remove();
                var myTemplate = Handlebars.compile($("#subDetail").html());
                $("#showSubDetail").append(myTemplate(data.data));
                $(".sub").fadeIn();
                $(".codeLayerbj").fadeIn();
            },
            error: function (data) {
                layer.msg("操作失败",{icon:5});
            }
        });
    })

    //确定按钮添加关闭事件
    $(".addDefine").click(function () {
        $(".close").trigger("click");
    })

    //重新编辑
    $(".c_bule").on("click",function () {
        $.ajax({
            type: 'GET',
            url: "/backend/course/speeAndDraw/isReEdit",
            data: {
                courseId: courseId
            },
            dataType: "json",
            success: function (data) {
                if(data.code == '00'){
                    window.location = '/backend/course/speeAndDraw/reEdit?courseId='+courseId+'&edit='+1
                }else {
                    layer.msg(data.message,{icon:5});
                }
            },
            error: function (data) {
                layer.msg("操作失败",{icon:5});
            }
        });
    })

    //激活/取消抽签结果
    $(".c_red").on("click",function () {
        var isBallot = $(this).attr("value")
        $.ajax({
            type: 'POST',
            url: "/backend/course/speeAndDraw/editIsBallot",
            data: {
                courseId: courseId,
                isBallot:isBallot
            },
            dataType: "json",
            success: function (data) {
                if(data.code == '00'){
                    layer.msg("操作成功",{icon:1});
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

    //判断
    Handlebars.registerHelper('if_eq',function(v1, v2,options){
        if(v1+v2 == v2){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });

});
