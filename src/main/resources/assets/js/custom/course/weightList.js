$(function () {
    var pageSize = 10;
    window.onload = function () {
        initweightList();
    }

    function initweightList() {
        //调用分页
        laypage.render({
            elem: 'list',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findweightList(obj.curr, pageSize);
            }
        });
    }

    function findweightList(pageNo, pageSize) {
        var name = $(".ipnSearch").val();
        var sex = $("#sex").val();
        var status = $("#status").val();
        var score = $("#score").val();
        var courseId = $("#courseId").val();
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/weightList",
            data: {
                name:name,
                sex: sex,
                status:status,
                score:score,
                courseId:courseId,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00") {
                    $("#list").show();
                    $(".no-content").remove();
                    //请求成功处理函数
                    var myTemplate = Handlebars.compile($("#weightList").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    if(data.data.length == 0){
                        $(".tableBox").after($("#weight-no-content").html());
                        $("#list").hide();
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
        var sex = $("#sex").val();
        var status = $("#status").val();
        var score = $("#score").val();
        var courseId = $("#courseId").val();
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/queryCount",
            data: {
                name:name,
                sex: sex,
                status:status,
                score:score,
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

    //表头更改事件监听
    $("[name='select']").change(function () {
        initweightList();
    })

    //搜索按钮事件
    $(".ipnBtn").on("click", function () {
        var pageNo = 1;
        var pageSize = 10;
        $("[name='select']").val("");
        findweightList(pageNo, pageSize);
    })


    $("tbody").on("click",".Ralink", function () {
        var adaNumber = $(this).parent().parent().find("#adaNumber").text();
        var courseId = $("#courseId").val();
        initScoreDetailList(adaNumber,courseId);
        var thmlH= $(window).height();
        var layerH = $(this).height();
        $(this).css("top",(thmlH-layerH)/2+"px");
    })

    //查详情数据
    function initScoreDetailList(adaNumber,courseId) {
        //调用分页
        laypage.render({
            elem: 'detail',
            count: getDetailTotalCount(adaNumber,courseId),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findScoreDetailtList(adaNumber,courseId,obj.curr, pageSize);
            }
        });
    }

    function findScoreDetailtList(adaNumber,courseId,pageNo, pageSize) {
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/searchScoreDetail",
            data: {
                courseId:courseId,
                adaNumber:adaNumber,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                $(".codeLayerbj,.RaDetails").fadeIn();
                var myTemplate = Handlebars.compile($("#scoreDetail").html());
                $("#scoredetailTbody tr").eq(0).siblings().remove();
                $("#scoredetailTbody").append(myTemplate(data.data));

            },
            error: function () {
            }
        });
    }

    function getDetailTotalCount(adaNumber,courseId) {
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/course/weight/queryScoreDetailCount",
            data: {
                courseId:courseId,
                adaNumber:adaNumber
            },
            dataType: "json",
            async: false,
            success: function (data) {
                count = JSON.parse(data.data);
            },
            error: function () {
            }
        });
        return count;
    }

    $("#export").on("click",function () {
        var name = $(".ipnSearch").val();
        var sex = $("#sex").val();
        var status = $("#status").val();
        var courseId = $("#courseId").val();
        location.href = "/backend/course/weight/exportWeightListExcel?courseId="+courseId+"&name="+name+"&sex="+sex+"&status="+status+"&courseId="+courseId;
    })

    Handlebars.registerHelper('compare',function(value,options){
        if(value == 2){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });

    //判断
    Handlebars.registerHelper('if_eq',function(v1, v2,options){
        if(v1+v2 == v2){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });

});
