$(function () {
    var name = "";
    var courseDocSearchName = "";

    window.onload = function () {
        if($("#hasDoc").val() == "true"){
            initCourseDocData();
        }
    }

    $(".swt_but").click(function () {

        initDocData();
    })

    function initDocData() {
        var pageSize = 8;
        //调用分页
        laypage.render({
            elem: 'docListPage',
            count: getDocCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                queryData(obj.curr,pageSize);
            }
        });
    }

    function getDocCount() {
        var count = 0;
        $.ajax({
            type:"post",
            url:"/backend/resource/doc/countDoc",
            contentType:"application/json",
            data:JSON.stringify({
                name:name,
                type:$("#type").val(),
                addType:$("#listAddType").val()
            }),
            async:false,
            success:function (data) {
                if(data.code == 00){
                    count = data.data;
                } else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
        return count;
    }

    function queryData(pageNo,pageSize) {
        $.ajax({
            type:"post",
            url:"/backend/resource/doc/docList",
            contentType:"application/json",
            data:JSON.stringify({
                name:name,
                type:$("#type").val(),
                addType:$("#listAddType").val(),
                orderType:$("#orderType").val(),
                pageNo:pageNo,
                pageSize:pageSize
            }),
            success:function (data) {
                if(data.code == 00){
                    $("#docListPage").show();
                    $(".no-content").remove();
                    var source = $("#docListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#docList").find("tr").eq(0).siblings().remove();
                    $("#docList").append(templateHtml);

                    var thmlH= $(window).height();
                    var layerH = $(".addTestQu").height();
                    $(".addTestQu").css("top",(thmlH-layerH)/2+"px");
                    $(".addTestQu").fadeIn();
                    $(".codeLayerbj").fadeIn();
                    if(data.data.list.length == 0){
                        $(".tableBox").after($("#doc-course-no-content").html());
                        $("#docListPage").hide();
                    }
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    }

    Handlebars.registerHelper("transformType",function (value) {
        if(value == 1){
            return "文档";
        } else if(value == 2){
            return "音频";
        } else if(value == 3){
            return "视频";
        } else {
            return "";
        }
    })

    Handlebars.registerHelper("addType",function (value) {
        if(value == 1){
            return "上传文件";
        }else if(value == 2){
            return "下载地址";
        }else {
            return "";
        }
    })

    Handlebars.registerHelper("formatDate",function (format,date) {
        return new Date(date).pattern(format);
    })

    Handlebars.registerHelper("subStr",function (size,value) {
        if(value.length <= size){
            return value;
        }
        var result = value.substring(0,size);
        result+="...";
        return result;
    })

    $("#docList").on("change","select",function () {
        initDocData();
    })

    $("#docListSearchBtn").click(function () {
        $("#docList").find("select").each(function () {
            $(this).find("option").eq(0).prop("selected","selected");
        })
        name = $("#name").val();
        initDocData();
    })

    $("tbody").on("click","input[type='checkbox']",function () {
        var checked = $(this).prop("checked");
        if(checked){
            $(this).parent('i').addClass('checkbox_bg_check');
        }else {
            $(this).parent('i').removeClass('checkbox_bg_check');
        }
    })

    $(".close").click(function () {
        var $parent = $(this).parents(".addTestQu");
        $parent.find("input[type='search']").val("");
        $parent.find("select").each(function () {
            $(this).find("option").eq(0).prop('selected',true);
        });
        name = "";
    })
    $("#cancelAdd").click(function () {
        $(".close").trigger("click");
    })

    //确定添加资料
    $("#confirmAdd").click(function () {
        var docIds = new Array();
        $("#docList").find("tr").eq(0).siblings().each(function () {
            docIds.push($(this).find("input[type='checkbox']:checked").data("id"));
        });
        $.ajax({
            type:"post",
            url:"/backend/course/doc/add",
            data:{
                courseId:$("#courseId").val(),
                docIds:docIds
            },
            traditional:true,
            dataType:"json",
            success:function (data) {
                if(data.code == '00'){
                    if ("success" == data.message) {
                        var source = $("#course-doc-add-show").html();
                        var template = Handlebars.compile(source);
                        var templateHtml = template(data.data);
                        // $(".close").trigger("click");
                        layer.closeAll();
                        layer.open({
                            type: 1
                            ,title: '提示信息'//不显示标题栏
                            ,closeBtn: 0
                            ,zIndex: 99999999
                            ,area: '500px;'
                            ,shade: 0.3
                            ,id: 'course-doc-add-show-0' //设定一个id，防止重复弹出
                            ,resize: false
                            ,btn: ['确定']
                            ,btnAlign: 'c'
                            ,content: templateHtml
                            ,yes: function(index, layero){
                                location.reload(true);
                            }
                        });
                    }else{
                        layer.msg(data.message,{icon:5});
                    }
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    })

    function initCourseDocData() {
        var pageSize = 10;
        //调用分页
        laypage.render({
            elem: 'courseDocPage',
            count: getCourseDocCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                queryCourseDocData(obj.curr,pageSize);
            }
        });
    }

    function getCourseDocCount() {
        var count = 0;
        $.ajax({
            type:"post",
            url:"/backend/course/doc/docCount",
            contentType:"application/json",
            data:JSON.stringify({
                name:courseDocSearchName,
                type:$("#courseDocType").val(),
                addType:$("#courseDocAddType").val(),
                shelve:$("#courseDocShelve").val(),
                courseId:$("#courseId").val()
            }),
            async:false,
            success:function (data) {
                if(data.code == 00){
                    count = data.data;
                } else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
        return count;
    }

    function queryCourseDocData(pageNo,pageSize) {
        $.ajax({
            type:"post",
            url:"/backend/course/doc/docList",
            contentType:"application/json",
            data:JSON.stringify({
                name:courseDocSearchName,
                type:$("#courseDocType").val(),
                addType:$("#courseDocAddType").val(),
                shelve:$("#courseDocShelve").val(),
                courseId:$("#courseId").val(),
                orderType:$("#courseDocOrderType").val(),
                pageNo:pageNo,
                pageSize:pageSize
            }),
            success:function (data) {
                if(data.code == 00){
                    $("#courseDocPage").show();
                    $(".no-content").remove();
                    var source = $("#courseDocListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#courseDocList").find("tr").eq(0).siblings().remove();
                    $("#courseDocList").append(templateHtml);
                    $("#selectAll").prop("checked", false).parent('i').removeClass('checkbox_bg_check');
                    if(data.data.list.length == 0){
                        $(".tableBox").after($("#doc-course-no-content").html());
                        $("#courseDocPage").hide();
                    }
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    }

    $("#courseDocList").on("change","select",function () {
        initCourseDocData();
    })

    $("#courseDocSearchBtn").click(function () {
        $("#courseDocList").find("select").each(function () {
            $(this).find("option").eq(0).prop("selected","selected");
        })
        courseDocSearchName = $("#courseDocName").val();
        initCourseDocData();
    })

    //监听checkbox
    $("#courseDocList").on("click","input[type='checkbox']",function () {
        var checked = $(this).prop("checked");
        if(checked){
            $(this).parent("i").addClass('checkbox_bg_check');
            var trLength = $("#courseDocList").find("tr").length-1;
            var checkedNum = $("#courseDocList").find("input[type='checkbox']:checked").length;
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
    
    function getCheckedDocIds() {
        var docIds = new Array();
        $("#courseDocList").find("input[type='checkbox']:checked").each(function () {
            docIds.push($(this).data("id"));
        })
        return docIds;
    }

    $("a[name='shelve']").click(function () {
        var docIds = getCheckedDocIds();
        if(docIds == null||docIds.length==0){
            layer.msg("请选择资料",{icon:5});
            return;
        }
        var shelve = $(this).data('value');
        $.ajax({
            type:"post",
            url:"/backend/course/doc/shelve",
            data:{
                courseId:$("#courseId").val(),
                shelve:shelve,
                docIds:docIds
            },
            traditional:true,
            dataType:"json",
            success:function (data) {
                if(data.code == 00){
                    layer.msg(shelve==1?"上架成功":"下架成功",{icon:1});
                    initCourseDocData();
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    })

    $("#deleteCourseDocBtn").click(function () {
        var docIds = getCheckedDocIds();
        deleteCourseDoc(docIds);
    })

    $("#courseDocList").on("click","a[name='deleteBtn']",function () {
        var docId = $(this).parents("tr").find("input[type='checkbox']").data("id");
        var docIds = new Array();
        docIds.push(docId);
        deleteCourseDoc(docIds);
    })

    function deleteCourseDoc(docIds) {
        if(docIds == null||docIds.length==0){
            layer.msg("请选择资料",{icon:5});
            return;
        }
        layer.confirm("是否确认删除",function () {
            $.ajax({
                type:"post",
                url:"/backend/course/doc/delete",
                data:{
                    courseId:$("#courseId").val(),
                    docIds:docIds
                },
                traditional:true,
                dataType:"json",
                success:function (data) {
                    if(data.code == 00){
                        layer.msg("删除成功",{icon:1});
                        location.reload();
                    }else {
                        layer.msg(data.message,{icon:5});
                    }
                }
            })
        })
    }

    $("#courseDocName").keydown(function (e) {
        if(e.keyCode == 13){
            $("#courseDocSearchBtn").trigger("click");
        }
    })

    $("#name").keydown(function (e) {
        if(e.keyCode == 13){
            $("#docListSearchBtn").trigger("click");
        }
    })
})