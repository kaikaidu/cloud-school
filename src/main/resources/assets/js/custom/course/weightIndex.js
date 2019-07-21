$(function () {

    $("#addNew").on("click",function () {
        window.location = '/backend/course/weight/addWeight?courseId='+$("#courseId").val();
    })

    // 鼠标移动到选择按钮，如果不能编辑显示提示信息
    $(document).on("mouseover", ".btnUpDid", function () {
        layer.tips("该评分模板已经有学员作答，不能被修改！", this, {
            tips: [1, '#3595CC'],
            time: 0
        });
    });
    // 鼠标移动出选择按钮，关闭提示信息
    $(document).on("mouseout", ".btnUpDid", function () {
        layer.closeAll();
    });

    var hasWeight = $("#hasWeight").val();
    if(hasWeight == "false"){
        return;
    }
    var courseId = $("#courseId").val();
    var pageNo = 1;
    var pageSize = 5;
    var sitemTempId;
    var name;
    var localTempId;//定义一个全局变量用来判断是否选择模板
    var localTempName;//名称

    //加载默认数据
    search();
    function search() {
        $(".ipnSearch").val();
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/searchWeightData",
            data: {
                courseId:courseId
            },
            dataType: "json",
            success: function (data) {
                var myTemplate = Handlebars.compile($("#initData").html());
                $("#search").html(myTemplate(data.data));
                localTempId = data.data.sitemTempId;
                localTempName =  data.data.name;
                initSelectStu(data.data.stu);
                initSelectTeac(data.data.teac);
            },
            error: function () {
            }
        })
    }
    $("#list").on("click","i",function () {
        $(this).parent().parent().siblings().find("i").removeClass("radio_bg_check");
        $(this).addClass("radio_bg_check");
    })

    //重新选择模板
    $("#search").on("click",".swt_but",function () {
        $(".ipnSearch").val("");
        initSitemList();
    })
    //弹出层结果集搜索
    $(".ipnBtn").on("click",function () {
        $("[name='createTimeType']").val("");
        initSitemList();
    })
    function initSitemList() {
        //调用分页
        laypage.render({
            elem: 'weightIndexPager',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findSitemList(obj.curr, pageSize);
            }
        });
    }

    function findSitemList(pageNo, pageSize) {
        var name = $(".ipnSearch").val();
        var createTimeType = $(".stySelS").find("option:selected").attr("value");
        var sitemTempId = $("#sitemTempId").val();
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/templetList",
            data: {
                name: name,
                createTimeType:createTimeType,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00") {
                    $("#weightIndexPager").show();
                    $(".no-content").remove();
                    var myTemplate = Handlebars.compile($("#temp").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    var thmlH = $(window).height();
                    var layerH = $(".addTestQu").height();
                    $(".addTestQu").css("top", (thmlH - layerH) / 2 + "px");
                    $(".addTestQu,.codeLayerbj").fadeIn();

                    $('[type="radio"]').each(function () {
                        if ($(this).prev().val() == sitemTempId) {
                            $(this).prop('checked', true);
                            $(this).parent().addClass("radio_bg_check");
                            sitemTempId = $(this).siblings().val();
                        }
                    });
                    if(data.data.length == 0){
                        $(".tableBox").after($("#weightIndex-no-content").html());
                        $("#weightIndexPager").hide();
                    }
                }else {
                    layer.msg(data.message, {icon: 5});
                }
            },
            error: function () {
            }
        });
    }
    function getTotalCount() {
        var name = $(".ipnSearch").val();
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/queryTempCount",
            data: {
                name: name,
            },
            dataType: "json",
            async: false,
            success: function (data) {
                count = JSON.parse(data);
            },
            error: function () {
            }
        });
        return count;
    }

    //表头更改事件监听
    $("[name='createTimeType']").change(function () {
        initSitemList();
    })

    /**
     * 预览
     */
    $("tbody").on("click", ".swt_but2",function () {
        sitemTempId = $(this).parent().siblings().find("#tempId").val();
        var name=$(this).parent().siblings().find("#name").val();
        if(getTempData(sitemTempId,name)){
            var swt_but_name=$(this).data("sum");
            if($(".codeLayer").length>0){
                $(".codeLayer").each(function(){
                    if($(this).data("sum")==swt_but_name){
                        var thmlH= $(window).height();
                        var layerH = $(this).height();
                        $(this).css("top",(thmlH-layerH)/2+"px");
                        $(this).fadeIn();
                        $(".codeLayerbj").fadeIn();
                    }
                })
            }
        }
    })

    function getTempData (sitemTempId,name) {
        var flag = true;
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/backend/course/weight/querySitemData',
            data: {
                sitemTempId: sitemTempId
            },
            async: false,
            success:function(data){
                if(data.message == "success"){
                    data.data.name = name;
                    var source = $("#temp_pre").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $(".temp_view").html(templateHtml);
                }else{
                    flag = false;
                }
            },
            error: function(){
                layer.msg("预览失败！", {icon: 5});
                flag = false;
            }
        })
        return flag;
    }

    //选择模板后确定
    $(".but_yes").on("click",function () {
        //循环
        var count = 0;
        $('[type="radio"]').each(function(){
            if ($(this).is(":checked")) {
                count++;
            }
        });

        var name = $(".radio_bg_check").find("#name").val();
        var tempId = $(".radio_bg_check").find("#tempId").val();
        if(tempId == null){
            name = localTempName;
            tempId = localTempId;
        }else {
            localTempName = name;
            localTempId = tempId;
        }
        var data = {};
        data.name = name;
        data.sitemTempId = tempId;
        var source = $("#chooseOther").html();
        var template = Handlebars.compile(source);
        var templateHtml = template(data);
        $("#otherTemp").html(templateHtml);
        $(".close").trigger("click");
    })

    //保存
    $("#save").on("click",function () {
        var weight={};
        var release = $("#isRelease").val();
        var sitemTempId = $("#sitemTempId").val();
        weight.courseId = courseId;
        weight.sitemTempId = sitemTempId;
        weight.stu = $("#stu").val();
        weight.teac = $("#teac").val();
        weight.release = release;
        if(!isNUll(weight)){
            return;
        }
        if(!checkWeight(weight.stu,weight.teac)){
            return;
        }
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/backend/course/weight/saveOrEditWeight',
            data: {data:JSON.stringify(weight)},
            async: false,
            success:function(data){
                if(data.code == '00'){
                    layer.msg("保存成功！",{icon: 1})
                }else {
                    layer.msg(data.message,{icon: 5})
                }
            },
            error: function(){
                layer.msg("保存失败！", {icon: 5});
            }
        })
    })

    //是否为正整数
    function isPositiveInteger(s){
        var re = /^[0-9]+$/ ;
        if(!re.test(s)){
            layer.msg("请检查输入的值",{icon:5});
            return false
        }
        return true;
    }

    function checkWeight(v1,v2) {
        if(Number(v1)+Number(v2) >1 || Number(v1)+Number(v2)<1){
            layer.msg("权重请设置百分百!",{icon:5});
            return false;
        }
        return true;
    }
    function isNUll(param) {
        if (isEmpty(param.sitemTempId)) {
            layer.msg("请选择模板!",{icon:5});
            return false;
        }
        if (isEmpty(param.stu)) {
            layer.msg("请填写学员占比!",{icon:5});
            return false;
        }
        if (isEmpty(param.teac)) {
            layer.msg("请填写讲师占比!",{icon:5});
            return false;
        }
        return true;
    }

    Handlebars.registerHelper('compare',function(value,options){
        if(value == 1){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });

    Handlebars.registerHelper("formatDate", function (format, date) {
        return new Date(date).pattern(format)
    })

    //注册索引+1的helper
    Handlebars.registerHelper("addOne",function(index){
        return index+1;
    });

    //初始化评分占比选项
    function initSelectStu(vr){
        $("#stu").empty();
        var item = new Option("请选择占比",-1);
        item.selected =true;
        $("#stu").append(item);
        var index = 0;
        for (var i = 0; i <= 20; i++) {
            var item = new Option(index + "%",new Number(0.01 * index).toFixed(2));
            if (index == vr) {
                item.selected = true;
            }
            $("#stu").append(item);
            index +=5;
        }
    }

    //初始化评分占比选项
    function initSelectTeac(vr){
        $("#teac").empty();
        var item = new Option("请选择占比",-1);
        item.selected =true;
        $("#teac").append(item);
        var index = 0;
        for (var i = 0; i <= 20; i++) {
            var item = new Option(index + "%",new Number(0.01 * index).toFixed(2));
            if (index == vr) {
                item.selected = true;
            }
            $("#teac").append(item);
            index +=5;
        }
    }
})

