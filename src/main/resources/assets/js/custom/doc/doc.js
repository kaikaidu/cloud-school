$(function () {
    var docUrl = "";

    var name = "";

    var editDocId = null;

    $('.courseUpload').Huploadify({
        auto:false,
        fileTypeExts:'*.doc;*.xls;*.ppt;*.pdf;*.docx;*.xlsx;*.pptx;*.mp4;*.mp3;*.DOC;*.XLS;*.PPT;*.PDF;*.DOCS;*.XLSX;*.PPTX;*.MP4;*.MP3',
        multi:false,
        formData:{directory:"doc"},
        fileSizeLimit: 10240,
        showUploadedPercent:true,//是否实时显示上传的百分比，如20%
        showUploadedSize:false,
        removeTimeout:9999999,
        buttonText:'选择文件',//上传按钮上的文字
        uploader:"/backend/upload",
        onUploadSuccess:function(file,data){
            var Data = JSON.parse(data);
            if(Data.code == '00'){
                docUrl = Data.data;
                layer.msg("文件上传成功!",{icon:1});
            }else {
                layer.msg(Data.message,{icon:5});
            }
        },
        onUploadError:function () {
            layer.msg("文件上传失败!",{icon:5});
        },
        onCancel:function(){
            docUrl = "";
        }
    });

    $("#saveDoc").click(function () {
        var name = $("#qiTie06").val();
        if(name==null||name==""){
            layer.msg("资料名称为必填项!",{icon:5});
            return;
        }
        var docType = $("#docType").val();
        if(docType==null||docType==""){
            layer.msg("资料类型为必填项!",{icon:5});
            return;
        }
        var addType = $("#addType").val();
        if(addType==null||addType==""){
            layer.msg("添加资料方式为必填项!",{icon:5});
            return;
        }
        var url = "";
        if(addType == 1){
            if(docUrl==null||docUrl==""){
                layer.msg("请先上传资料!",{icon:5});
                return;
            }
            url = docUrl;
        }else{
            var docLink = $("#qiTie0602").val();
            if(docLink==null||docLink==""){
                layer.msg("资料下载地址链接为必填项!",{icon:5});
                return;
            }
            var strRegex=/https?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
            var re=new RegExp(strRegex);
            if (docLink.indexOf("https") == -1 || !re.test(docLink)){
                layer.msg("资料下载地址不正确",{icon:5});
                return;
            }
            if(docLink.length > 300){
                layer.msg("资料下载地址不得超过300个字符",{icon:5});
                return;
            }
            url = docLink;
        }
        $.ajax({
            type:"post",
            url:"/backend/resource/doc/saveDoc",
            contentType:"application/json",
            data:JSON.stringify({
                name:name,
                url:url,
                type:docType,
                addType:addType,
                id:editDocId
            }),
            success:function (data) {
                if(data.code==00){
                    layer.msg("保存成功",{time:500,icon:1},function () {
                        $(".close").trigger("click");
                        location.reload();
                    });
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        });
    })

    $("#cancelDoc").click(function () {
        $(".close").trigger("click");
    })
    
    //清空数据
    $(".close").click(function () {

        var $parent = $(this).parents(".addTestQu");
        $parent.find("input[type='text']").val("");
        $parent.find("select").each(function () {
            $(this).find("option").eq(0).prop("selected",true);
        })
        // $("#addType").trigger("change");
        var value = $("#addType").val();
        if(value == 1){
            $("#fileLink").hide();
            $("#uploadFile").show();
        } else if(value == 2){
            $("#fileLink").show();
            $("#uploadFile").hide();
        } else {
            $("#fileLink").show();
            $("#uploadFile").show();
        }
        $(".delfilebtn").trigger('click');
        editDocId = null;
        docUrl = "";
    })

    window.onload = function () {
        initDocData();
    }

    function initDocData() {
        var pageSize = 10;
        //调用分页
        laypage.render({
            elem: 'docDefaultPager',
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
                name:$("#name").val(),
                type:$("#type").val(),
                addType:$("#listAddType").val(),
                orderType:$("#orderType").val(),
                pageNo:pageNo,
                pageSize:pageSize
            }),
            success:function (data) {
                if(data.code == 00){
                    $("#docDefaultPager").show();
                    $(".no-content").remove();
                    var source = $("#docListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("tbody").find("tr").eq(0).siblings().remove();
                    $("tbody").append(templateHtml);
                    $("#selectAll").prop("checked", false).parent('i').removeClass('checkbox_bg_check');
                    if(data.data.list.length == 0){
                        $(".tableBox").after($("#doc-no-content").html());
                        $("#docDefaultPager").hide();
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

    $("select").change(function () {
        initDocData();
    })
    
    $("#searchBtn").click(function () {
        $("tbody").find("select").each(function () {
            $(this).find("option").eq(0).prop("selected","selected");
        })
        name = $("#name").val();
        initDocData();
    })

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

    $("tbody").on("click","[name='deleteDoc']",function () {
        var docId = $(this).parents("tr").find("input[type='checkbox']").data("id");
        var docIds = new Array();
        docIds.push(docId);
        deleteDoc(docIds);
    })

    //点击删除提示详情
    $("body").on("click",".remove-tip",function () {
        var docId = $(this).data("id");
        $.ajax({
            url : "/backend/resource/doc/course/list" ,
            type : "post" ,
            dataType : "json",
            data : {"docId":docId} ,
            success : function (res) {
                if ("success" == res.message) {
                    var source = $("#doc-remove-detail-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(res.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'doc_remove_detail_show' //设定一个id，防止重复弹出
                        ,resize: false
                        ,btn: ['确定']
                        ,btnAlign: 'c'
                        ,content: templateHtml
                        ,yes: function(index, layero){
                            layer.close(index);
                        }
                    });
                }
            }
        });
    })

    function deleteDoc(docIds) {
        layer.confirm("是否确认删除",function (index) {
            $.ajax({
                type:"post",
                url:"/backend/resource/doc/delete",
                contentType:"application/json",
                data:JSON.stringify(docIds),
                success:function (data) {
                    if(data.code == 00){
                        layer.close(index);
                        if ("success" == data.message) {
                            var source = $("#doc-remove-show").html();
                            var template = Handlebars.compile(source);
                            var templateHtml = template(data.data);
                            layer.open({
                                type: 1
                                ,title: '提示信息'//不显示标题栏
                                ,closeBtn: 0
                                ,area: '500px;'
                                ,shade: 0.3
                                ,id: 'doc_detail_show' //设定一个id，防止重复弹出
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
                            layer.msg(data.message,{icon:5});
                        }
                    }else {
                        layer.msg(data.message,{icon:5});
                    }
                }
            })
        })
    }

    $("#deleteDocs").click(function () {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        var docIds = new Array();
        $selected.each(function () {
            docIds.push($(this).data("id"));
        })
        if(docIds.length==0){
            layer.msg("请选择资料",{icon:5});
            return;
        }
        deleteDoc(docIds);
    })

    $("[name='addDocBtn']").click(function () {
        $("#addTitle").html("新增下载资料");
    })

    $("tbody").on("click","[name='editDoc']",function () {
        $("#addTitle").html("编辑下载资料");
        editDocId = $(this).parents("tr").children("td").eq(0).find("input[type='checkbox']").data("id");
        $.ajax({
            type:"get",
            cache:false,
            url:"/backend/resource/doc/selectById",
            data:{docId:editDocId},
            success:function (data) {
                if(data.code==00){
                    var doc = data.data;
                    renderDoc(doc);
                    var swt_but_name=$(".swt_but").data("sum");
                    if($(".addTestQu").length>0){
                        $(".addTestQu").each(function(){
                            if($(this).data("sum")==swt_but_name){
                                var thmlH= $(window).height();
                                var layerH = $(this).height();
                                $(this).css("top",(thmlH-layerH)/2+"px");
                                $(this).fadeIn();
                                $(".codeLayerbj").fadeIn();
                                // $(".codeLayer,.codeLayerbj").fadeIn();
                            }

                        })
                    }
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    })
    
    function renderDoc(doc) {
        $("#qiTie06").val(doc.name);
        $("#docType").val(doc.type);
        $("#addType").val(doc.addType);
        docUrl = doc.url;
        $("#addType").trigger("change");
        if(doc.addType == 2){
            $("#qiTie0602").val(doc.url);
        }
    }
    
    $("#addType").change(function () {
        var value = $("#addType").val();
        if(value == 1){
            $("#fileLink").hide();
            $("#uploadFile").show();
        } else if(value == 2){
            $("#fileLink").show();
            $("#uploadFile").hide();
        } else {
            $("#fileLink").show();
            $("#uploadFile").show();
        }
    })

    $("#name").keydown(function (e) {
        if(e.keyCode == 13){
            $("#searchBtn").trigger("click");
        }
    })
})
