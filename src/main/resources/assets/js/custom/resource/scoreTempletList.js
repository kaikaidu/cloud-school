$(function () {
    var pageSize = 10;
    window.onload = function () {
        initCourseList();
    }

    function initCourseList() {
        //调用分页
        laypage.render({
            elem: 'scoreTemplateListPager',
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
        var createTimeTye = $("#createTimeType").val();
        $.ajax({
            type: 'POST',
            url: "/backend/resource/sitemTemp/templetList",
            data: {
                name: name,
                createTimeTye:createTimeTye,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00") {
                    $("#scoreTemplateListPager").show();
                    $(".no-content").remove();
                    $("#selectAll").prop("checked", false).parent('i').removeClass('checkbox_bg_check');
                    //请求成功处理函数
                    var myTemplate = Handlebars.compile($("#registerList").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    if(data.data.length == 0){
                        $(".tableBox").after($("#scoreTemp-no-content").html());
                        $("#scoreTemplateListPager").hide();
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
            url: "/backend/resource/sitemTemp/queryCount",
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

    //搜索按钮事件
    $(".ipnBtn").on("click", function () {
        $("[name='createTimeType']").val("");
        initCourseList();
    })

    //表头更改事件监听
    $("[name='createTimeType']").change(function () {
        initCourseList();
    })

    //单独删除操作
    $("tbody").on("click", "#sitemDel", function () {
        var sitemIds = $(this).parents("tr").children('td').eq(0).text();
        clearSelected(sitemIds);
    })

    //批量删除
    $("#del").on("click", function () {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        if($selected.length == 0){
            layer.msg("请选择模板!",{icon:5})
            return;
        }
        clearSelected(getCheckedSitemIds());
    })

    //获取id的集合
    function getCheckedSitemIds() {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        var sitemTempIds = "";
        $selected.each(function () {
            var sitemTempId = $(this).parents("tr").children('td').eq(0).text();
            sitemTempIds = sitemTempIds + sitemTempId + ','
        })
        return sitemTempIds;
    }

    /**
     * 删除数据
     * @param sitemTempIds
     */
    function clearSelected(sitemIds) {
        layer.confirm('确认要删除吗？',function(index){
            $.ajax({
                type: 'POST',
                dataType: 'json',
                url: '/backend/resource/sitemTemp/batchDelSitemTemp',
                data:{
                    sitemIds: sitemIds
                },
                success:function(data){
                    layer.close(index);
                    if(data.message == "success"){
                        var source = $("#testTemp-remove-show").html();
                        var template = Handlebars.compile(source);
                        var templateHtml = template(data.data);
                        layer.open({
                            type: 1
                            ,title: '提示信息'//不显示标题栏
                            ,closeBtn: 0
                            ,area: '600px;'
                            ,shade: 0.3
                            ,id: 'test_temp_remove_show' //设定一个id，防止重复弹出
                            ,resize: false
                            ,btn: ['确定']
                            ,btnAlign: 'c'
                            ,content: templateHtml
                            ,yes: function(index, layero){
                                layer.close(index);
                                location.reload(true);
                            }
                        });
                    }else{
                        layer.close(index);
                        layer.msg("删除失败！", {icon: 5});
                    }
                },
                error: function(){
                    layer.close(index);
                    layer.msg("删除失败！", {icon: 5});
                }
            });
        });
    }

    /**
     * 点击详情弹出层
     */
    $(document).on("click", ".remove-tip", function () {
        var sitemTempId = $(this).data("id");
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/backend/resource/sitemTemp/batchDelSitemTempDetail',
            data:{
                sitemTempId: sitemTempId
            },
            success:function(data){
                if(data.message == "success"){
                    var source = $("#testTemp-remove-detail-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'test_temp_remove_detail_show' //设定一个id，防止重复弹出
                        ,resize: false
                        ,btn: ['确定']
                        ,btnAlign: 'c'
                        ,content: templateHtml
                        ,yes: function(index, layero){
                            layer.close(index);
                        }
                    });
                }else{
                    layer.msg("查询详情失败！", {icon: 5});
                }
            },
            error: function(){
                layer.msg("查询详情失败！", {icon: 5});
            }
        });
    })

    //编辑
    $("tbody").on("click", ".edit", function () {
        var sitemTempId = $(this).parents("tr").children('td').eq(0).text();
        window.location = "/backend/resource/sitemTemp/editTemplet?sitemTempId=" + sitemTempId +"&type=edit";
    })

    //克隆
    $("tbody").on("click", "#copy", function () {
        var sitemTempId = $(this).parents("tr").children('td').eq(0).text();
        window.location = "/backend/resource/sitemTemp/editTemplet?sitemTempId=" + sitemTempId +"&type=copy";
    })

    //新增模板按钮
    $("#addTemp").on("click", function () {
        window.location.href = "/backend/resource/sitemTemp/addTemplet";
    })

    //预览
    $("tbody").on("click", ".swt_but",function () {
        var sitemTempId = $(this).parents("tr").children('td').eq(0).text();
        var name = $(this).parents("tr").children('td').eq(1).text();
        if(getTempData(sitemTempId,name)){
            var swt_but_name=$(this).data("sum");
            if($(".addTestQu").length>0){
                $(".addTestQu").each(function(){
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
            url: '/backend/resource/sitemTemp/querySitemData',
            data: {
                sitemTempId: sitemTempId
            },
            async: false,
            success:function(data){
                if(data.message == "success"){
                    data.data.name = name;
                    var source = $("#temp-view").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $(".temp-view").html(templateHtml);
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

    //监听checkbox
    $("tbody").on("click","input[type='checkbox']",function () {
        var checked = $(this).prop("checked");
        if(checked){
            $(this).parent("i").addClass('checkbox_bg_check');
            var trLength = $("tbody").find("tr").length-1;
            var checkedNum = $("tbody").find("input[type='checkbox']:checked").length;
            if(trLength == checkedNum){
                $("#selectAll").prop("checked",true);
                $("#selectAll").parent().addClass("checkbox_bg_check");
            }
        }else{
            $(this).parent("i").removeClass('checkbox_bg_check');
            $("#selectAll").prop("checked",false);
            $("#selectAll").parent("i").removeClass('checkbox_bg_check');
        }
    })

    Handlebars.registerHelper("formatDate", function (format, date) {
        return new Date(date).pattern(format);
    })

    //注册索引+1的helper
    Handlebars.registerHelper("addOne",function(index){
        return index+1;
    });
})
