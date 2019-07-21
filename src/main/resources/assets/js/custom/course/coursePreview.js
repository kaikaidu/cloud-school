$(function () {
    //点击按钮出现弹层str
    $("#courseList").on("click",".swt_but",function () {
        var courseId = $(this).parents("tr").find("input[type='checkbox']").data("id");
        coursePreview(courseId);
        signUpList(courseId);

        var swt_but_name=$(this).data("sum");
        if($(".codeLayer").length>0){
            $(".codeLayer").each(function(){
                if($(this).data("sum")==swt_but_name){
                    var thmlH= $(window).height();
                    var layerH = $(this).height();
                    $(this).css("top",(thmlH-layerH)/2+"px");
                    $(this).fadeIn();
                    $(".codeLayerbj").fadeIn();
                    // $(".codeLayer,.codeLayerbj").fadeIn();
                    $(".w_yl_com_bt").children("li").removeClass("cur");
                    $(".w_yl_com_bt").children("li").eq(0).addClass("cur");
                    $(".w_yl_com00").children().css("display","none");
                    $(".w_yl_com00").children().eq(0).css("display","block");
                }
            })
        }
    })

    // 点击弹层的关闭按钮时str
    $(".close,.but_none").click(function(){
        $("#signUpList").find("tr").not(":first").remove();
        $("#certSettingList").find("tr").not(":first").remove();
        $("#invesList").find("tr").not(":first").remove();
        $("#testOnlineList").find("tr").not(":first").remove();
        $("#drawList").find("tr").not(":first").remove();
        $("#scoreList").find("tr").not(":first").remove();
        $("#docList").find("tr").not(":first").remove();
    });

    function coursePreview(courseId) {
        $.ajax({
            type:"get",
            url:"/backend/course/preview",
            contentType:"application/json",
            data:{courseId:courseId},
            success:function (data) {
                if(data.code==00){
                    var source = $("#coursePreviewTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#coursePreview").html(templateHtml);
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    }

    Handlebars.registerHelper('formatTeachers',function (teachers) {
        var teacherStr = "";
        $.each(teachers,function (index,teacher) {
            if(index<teachers.length-1){
                teacherStr = teacherStr+teacher+"、";
            }else {
                teacherStr+=teacher;
            }
        })
        return teacherStr;
    })

    //签到
    $("#signUp").click(function () {
        var courseId = $("#courseId1").text();
        signUpList(courseId);
    })

    function signUpList(courseId){
        var pageSize = 5;
        //调用分页
        laypage.render({
            elem: 'signUpPage',
            count: signUpTotalCount(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                signUpQueryData(courseId,obj.curr,pageSize);
            }
        });
    }
    
    function signUpTotalCount(courseId) {
        var count=0;
        $.ajax({
            type:"post",
            url:"/backend/course/registerManage/queryCount",
            data:{courseId:courseId},
            dataType:"json",
            async:false,
            success:function (data) {
                if(!isNaN(data)){
                    count = data;
                }
            }
        });
        return count;
    }
    
    function signUpQueryData(courseId,pageNo,pageSize) {
        $.ajax({
            type: 'POST',
            url: "/backend/course/registerManage/registerList",
            data: {
                courseId:courseId,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code==00){
                    var source = $("#signUpListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#signUpList tr").eq(0).siblings().remove();
                    $("#signUpList").append(templateHtml);
                }
            }
        })
    }

    //测试
    $("#testOnline").click(function () {
        var courseId = $("#courseId1").text();
        testOnlinePage(courseId);
    })

    function testOnlinePage(courseId) {
        var pageSize = 5;
        //调用分页
        laypage.render({
            elem: 'testOnlinePage',
            count: testOnlineCount(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                testOnLineList(courseId,obj.curr,pageSize);
            }
        });
    }

    function testOnlineCount(courseId) {
        var count = 0;
        $.ajax({
            type:"get",
            url:"/backend/course/test/online/count",
            data:{
                search:"",
                courseId:courseId,
                state:0
            },
            async:false,
            success:function (data) {
                if(data.code==00){
                    count = data.data;
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        });
        return count;
    }
    
    function testOnLineList(courseId,pageNo,pageSize) {
        $.ajax({
            type:"get",
            url:"/backend/course/test/online/list",
            data:{
                search:"",
                courseId:courseId,
                state:0,
                pageNo:pageNo,
                pageSize:pageSize
            },
            success:function (data) {
                if(data.code==00){
                    var source = $("#testOnlineTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#testOnlineList tr").eq(0).siblings().remove();
                    $("#testOnlineList").append(templateHtml);
                }else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    }

    //课程资料
    $("#doc").click(function () {
        initCourseDocData();
    })

    function initCourseDocData() {
        var pageSize = 5;
        //调用分页
        laypage.render({
            elem: 'docPage',
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
                courseId:$("#courseId1").text()
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
                courseId:$("#courseId1").text(),
                pageNo:pageNo,
                pageSize:pageSize
            }),
            success:function (data) {
                if(data.code == 00){
                    var source = $("#docListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#docList").find("tr").eq(0).siblings().remove();
                    $("#docList").append(templateHtml);
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
            return "上传地址";
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

    //抽签

    $("#draw").click(function(){
        var courseId = $("#courseId1").text();
        initDrawData(5,courseId);
    })

    function initDrawData(pageSize,courseId){
        //调用分页
        laypage.render({
            elem: 'drawPage',
            count: getDrawCount(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findDrawByName(obj.curr,pageSize,courseId);
            }
        });
    }

    //根枯姓名查询分班列表
    function findDrawByName(pageNo,pageSize,courseId){
        $.ajax({
            url : "/backend/course/speeAndDraw/selectDrawResulPre" ,
            type : "get" ,
            dataType : "json" ,
            data : {"pageNo":pageNo,"pageSize":pageSize,"courseId":courseId} ,
            success : function(res) {
                console.log(res.data);
                if(res.code == 00){
                    var source = $("#drawListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(res.data);
                    $("#drawList").find("tr").eq(0).siblings().remove();
                    $("#drawList").append(templateHtml);
                }else {
                    layer.msg(res.message,{icon:5});
                }
            }
        });
    }

    //获取总数
    function getDrawCount(courseId){
        var count = 0;
        $.ajax({
            type: 'GET',
            dataType: 'json',
            data : {
                "courseId":courseId
            },
            url: '/backend/course/speeAndDraw/selectDrawResulPreCount',
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

    //问卷
    $("#inves").click(function () {
        var courseId = $("#courseId1").text();
        initPager(5,courseId);
    })

    // 设置tf_state方法
    Handlebars.registerHelper('tf_state', function(v1) {
        if(v1 == 1){
            return "上架";
        }else if(v1 == 2){
            return "下架";
        }else{
            return "未定义";
        }
    });

    function invesdataQuery(pageNo, pageSize,courseId) {
        $.ajax({
            type: 'GET',
            dataType: 'json',
            data:{
                courseId: courseId,
                search: null,
                state: 0,
                pageNo: pageNo,
                pageSize: pageSize
            },
            url: '/backend/course/inves/online/list',
            success: function (data) {
                if(data.message == "success"){
                    var source = $("#invesTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);

                    $("#invesList tr").eq(0).siblings().remove();
                    $("#invesList").append(templateHtml);
                }
            }
        });
    }

    /**
     * 初始化分页插件
     * @param pageSize
     */
    function initPager(pageSize,courseId){
        //调用分页
        laypage.render({
            elem: 'invesPage',
            count: getInvesTotalData(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                invesdataQuery(obj.curr,pageSize,courseId);
            }
        });
    }

    /**
     * 第一次查询总数
     * @returns {number}
     */
    function getInvesTotalData(courseId) {
        var count = 0;
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '/backend/course/inves/online/count',
            async: false,
            data: {search: null, state: 0, courseId: courseId},
            success: function(data){
                if(data.message == "success"){
                    count = data.data;
                }else{
                }
            },
            error: function(){
            }
        });
        return count;
    }

    //评分
    $("#score").click(function () {
        var courseId = $("#courseId1").text();
        initweightList(courseId);
    })
    
    function initweightList(courseId) {
        var pageSize = 5;
        //调用分页
        laypage.render({
            elem: 'scorePage',
            count: getTotalCount(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findweightList(obj.curr,pageSize,courseId);
            }
        });
    }

    function findweightList(pageNo, pageSize,courseId) {
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/weightList",
            data: {
                score:0,
                courseId:courseId,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                console.log(data.data);
                //请求成功处理函数
                var myTemplate = Handlebars.compile($("#scoreTemplate").html());
                $("#scoreList tr").eq(0).siblings().remove();
                $("#scoreList").append(myTemplate(data.data));
            },
            error: function () {
            }
        });
    }

    function getTotalCount(courseId) {
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/queryCount",
            data: {
                score:0,
                courseId:courseId
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

    //证书设置
    $("#certSetting").click(function () {
        var courseId = $("#courseId1").text();
        initCertSettingList(courseId);
    })

    function initCertSettingList(courseId) {
        var pageSize = 5;
        //调用分页
        laypage.render({
            elem: 'certSettingPage',
            count: getCertSettingTotalCount(courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findCertSettingList(obj.curr,pageSize,courseId);
            }
        });
    }

    function findCertSettingList(pageNo, pageSize,courseId) {
        $.ajax({
            type:"post",
            url: "/backend/course/certset/findUserCertList",
            contentType:"application/json",
            data: JSON.stringify({
                courseId:courseId,
                pageNo: pageNo,
                pageSize: pageSize
            }),
            success: function (data) {
                console.log(data.data);
                //请求成功处理函数
                var myTemplate = Handlebars.compile($("#certSettingTemplate").html());
                $("#certSettingList tr").eq(0).siblings().remove();
                $("#certSettingList").append(myTemplate(data.data.dataList));
            },
            error: function () {
            }
        });
    }

    function getCertSettingTotalCount(courseId) {
        var count = 0;
        $.ajax({
            type: "get",
            url: "/backend/course/certset/findUserCertListCount",
            data:{
                courseId:courseId
            },
            async: false,
            success: function (data) {
                count = data.data;
            },
            error: function () {
            }
        });
        return count;
    }

})
