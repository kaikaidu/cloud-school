$(function () {

    var pageSize = 10;

    window.onload = function () {
        initCourseList();
    }

    function initCourseList() {
        //调用分页
        laypage.render({
            elem: 'registerPager',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findSignList(obj.curr, pageSize);
            }
        });
    }

    //表头事件改变
    $("[name='select']").change(function () {
        initCourseList();
    })

    //搜索事件
    $(".ipnBtn").on("click", function () {
        var pageNo=1;
        var pageSize=10;
        $("[name='select']").val("");
        initCourseList();
    })

    function findSignList(pageNo,pageSize) {
        var name = $(".ipnSearch").val();
        var sex = $("#sex").val();
        var registerStatus = $("#registerStatus").val();
        var courseId =  $("#courseId").val();
        $.ajax({
            type: 'POST',
            url: "/backend/course/registerManage/registerList",
            data: {
                name: name,
                sex: sex,
                registerStatus: registerStatus,
                courseId:courseId,
                pageNo:pageNo,
                pageSize:pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00"){
                    $("#registerPager").show();
                    $(".no-content").remove();
                    $("#selectAll").parent("i").removeClass('checkbox_bg_check');
                    //请求成功处理函数
                    var myTemplate = Handlebars.compile($("#registerList").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    //列表无数据时不显示已签到按钮
                    if(data.data.length==0){
                        $("#registerUp").hide();
                        $(".tableBox").after($("#register-no-content").html());
                        $("#registerPager").hide();
                    }else {
                        $("#registerUp").show();
                    }
                }else {
                    layer.msg(data.message, {icon: 5});
                }
            }
        });
    }

    function getTotalCount() {
        var name = $(".ipnSearch").val();
        var sex = $("#sex").val();
        var registerStatus = $("#registerStatus").val();
        var courseId =  $("#courseId").val();
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/course/registerManage/queryCount",
            data: {
                name: name,
                sex: sex,
                registerStatus: registerStatus,
                courseId:courseId,
            },
            dataType: "json",
            async:false,
            success: function (data) {
                count = JSON.parse(data);
            },
            error: function () {
            }
        });
        return count;
    }

    //签到按钮
    $("#registerUp").on("click",function () {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        if($selected.length == 0){
            layer.msg("请选择学员!",{icon:5})
            return;
        }
        var courseId =  $("#courseId").val();
        $.ajax({
            type: 'POST',
            url: "/backend/course/registerManage/registerUp",
            data: {
                uIds:getCheckedIds(),
                courseId: courseId
            },
            dataType: "json",
            async:false,
            success: function (data) {
                if(data.code == '00'){
                    layer.msg("签到成功!",{icon:1});
                    findSignList(1,10);
                    $("#selectAll").prop("checked", false)
                    $("i").removeClass('checkbox_bg_check');
                }else {
                    layer.msg(data.message);
                    $("#selectAll").prop("checked", false);
                    $("i").removeClass('checkbox_bg_check');
                }

            },
            error: function () {
                layer.msg("选中的学员都已经签到!",{icon:5});
            }
        });
    })

    function getCheckedIds() {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        var uIds = "";
        $selected.each(function () {
            var uid = $(this).parents("tr").children('td').eq(0).text();
            uIds=uIds+uid+','
        })
        return uIds;
    }
    
    //查看二维码
    $(".btnUp").on("click",function () {
        var courseId =  $("#courseId").val();
        $.ajax({
            type: 'GET',
            url: "/backend/course/registerManage/findQRCode",
            data: {
                courseId: courseId
            },
            dataType: "json",
            async:false,
            success: function (data) {
                console.log(data);
                if(data.code == '00'){
                    $("#QRCode").attr("src",data.data);
                    $("#QRCodeDownload").attr("href",data.data);
                    $("#QRCodeDownload").attr("download",data.data);
                }else {
                }
            },
            error: function () {
            }
        });
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

    Handlebars.registerHelper("equal",function(v1,options){
        if(v1 == '已签到'){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    })
})