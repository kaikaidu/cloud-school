$(function () {
    var pageNo = 1;
    var pageSize = 10;
    window.onload = function () {
        initadminList();
    }

    function initadminList() {
        //调用分页
        laypage.render({
            elem: 'operationLogListPager',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findadminList(obj.curr, pageSize);
            }
        });
    }

    function findadminList(pageNo, pageSize) {
        $.ajax({
            type: 'POST',
            url: "/backend/platform/operationLogList",
            data: {
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00") {
                    console.log(data.data);
                    $("#operationLogListPager").show();
                    $(".no-content").remove();
                    //请求成功处理函数
                    var myTemplate = Handlebars.compile($("#operationLogList").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    if(data.data.length == 0){
                        $(".tableBox").after($("#operationLogList-no-content").html());
                        $("#operationLogListPager").hide();
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
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/platform/queryOperationLogListCount",
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

    Handlebars.registerHelper('compare',function(value,options){
        if(value == 0){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });
})
