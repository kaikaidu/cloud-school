$(function () {
    var selectTitle = "";

    window.onload = function () {
        initCourseList();
    }

    function queryData(pageNo, pageSize) {
        var param = {
            title: selectTitle,
            createTime: $("#multi01").val(),
            startTime: $("#multi02").val(),
            endTime: $("#multi03").val(),
            label: $("#selectLabel").val(),
            shelve: $("#selectShelve").val(),
            createTimeOrder: $("#ctorder").val(),
            pageNum: pageNo,
            pageSize: pageSize
        };
        $.ajax({
            type: "post",
            url: "/backend/course/courseList",
            contentType: "application/json",
            data: JSON.stringify(param),
            success: function (data) {
                if (data.code == "00") {
                    $("#courseListPage").show();
                    $(".no-content").remove();
                    var source = $("#courseListTemplate").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    $("#courseList tr").eq(0).siblings().remove();
                    $("#courseList").append(templateHtml);
                    //渲染页面，清除全选checkbox
                    $("#selectAll").attr("checked", false);
                    $("#selectAll").parent("i").removeClass('checkbox_bg_check');
                    if(data.data.dataList.length == 0){
                        $(".tableBox").after($("#course-no-content").html());
                        $("#courseListPage").hide();
                    }
                } else {
                    layer.msg(data.message, {icon: 5});
                }
            }
        })
    }

    function initCourseList() {
        var pageSize = 12;
        //调用分页
        laypage.render({
            elem: 'courseListPage',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function (obj, first) {
                queryData(obj.curr, pageSize);
            }
        });
    }

    function getTotalCount() {
        var count = 0;
        var param = {
            title: selectTitle,
            createTime: $("#multi01").val(),
            startTime: $("#multi02").val(),
            endTime: $("#multi03").val(),
            label: $("#selectLabel").val(),
            shelve: $("#selectShelve").val(),
        };
        $.ajax({
            type: "post",
            url: "/backend/course/courseCount",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(param),
            success: function (data) {
                if (data.code == "00") {
                    count = data.data;
                }
            }
        });
        return count;
    }

    $("[name='selectType']").change(function () {
        initCourseList();
    })

    //（年月日）双面板
    $("#multi01").jeDate({
        multiPane: false,
        minDate: '2017-06-16 10:20:25',
        maxDate: '2025-06-16 18:30:35',
        format: 'YYYY-MM-DD',
        clearfun: function () {
            initCourseList();
        },
        okfun: function () {
            initCourseList();
        }
    });
    $("#multi02").jeDate({
        multiPane: false,
        minDate: '2017-06-16 10:20:25',
        maxDate: '2025-06-16 18:30:35',
        format: 'YYYY-MM-DD',
        clearfun: function () {
            initCourseList();
        },
        okfun: function () {
            initCourseList();
        }
    });
    $("#multi03").jeDate({
        multiPane: false,
        minDate: '2017-06-16 10:20:25',
        maxDate: '2025-06-16 18:30:35',
        format: 'YYYY-MM-DD',
        clearfun: function () {
            initCourseList();
        },
        okfun: function () {
            initCourseList();
        }
    });

    $("#searchBtn").click(function () {
        selectTitle = $("#selectTitle").val();
        $("[name='selectType']").val("");
        initCourseList();
    })


    function getCheckedIds() {
        var $selected = $("#courseList").find("input[type='checkbox']:checked");
        var courseIds = new Array();
        $selected.each(function () {
            courseIds.push($(this).data("id"));
        });
        return courseIds;
    }

    $("#shelfBatchBtn").click(function () {
        var courseIds = getCheckedIds();
        if (courseIds.length == 0) {
            layer.msg("请选择课程", {icon: 5});
            return;
        }
        $.ajax({
            type: "post",
            url: "/backend/course/shelve",
            contentType: "application/json",
            data: JSON.stringify(courseIds),
            success: function (data) {
                if (data.code == 00) {
                    layer.msg("发布成功", {icon: 1});
                    initCourseList();
                } else {
                    layer.msg(data.message, {icon: 5});
                }
            }
        });
    })

    $("#offShelfBatchBtn").click(function () {
        var courseIds = getCheckedIds();
        if (courseIds.length == 0) {
            layer.msg("请选择课程", {icon: 5});
            return;
        }
        $.ajax({
            type: "post",
            url: "/backend/course/offShelve",
            contentType: "application/json",
            data: JSON.stringify(courseIds),
            success: function (data) {
                if (data.code == 00) {
                    layer.msg("取消发布成功", {icon: 1});
                    initCourseList();
                } else {
                    layer.msg(data.message, {icon: 5});
                }
            }
        });
    })

    function deleteCourses(param) {
        if (param.length == 0) {
            layer.msg("请选择课程", {icon: 5});
            return;
        }
        layer.confirm("是否确认删除", function () {
            $.ajax({
                type: "post",
                url: "/backend/course/delete",
                contentType: "application/json",
                data: JSON.stringify(param),
                success: function (data) {
                    if (data.code == 00) {
                        layer.msg("删除成功", {icon: 1});
                        initCourseList();
                    } else {
                        layer.msg(data.message, {icon: 5});
                    }
                }
            });
        })
    }

    $("#deleteBatchBtn").click(function () {
        var courseIds = getCheckedIds();
        deleteCourses(courseIds);
    })

    $("#courseList").on("click", "[name='deleteBtn']", function () {
        var id = $(this).parent("td").parent("tr").find("input[type='checkbox']").data("id");
        var courseId = new Array();
        courseId.push(id);
        deleteCourses(courseId);
    })

    //监听checkbox
    $("#courseList").on("click", "input[type='checkbox']", function () {
        var checked = $(this).prop("checked");
        if (checked) {
            $(this).parent("i").addClass('checkbox_bg_check');
            var trLength = $("#courseList").find("tr").length - 1;
            var checkedNum = $("#courseList").find("input[type='checkbox']:checked").length;
            if (trLength == checkedNum) {
                $("#selectAll").prop("checked", true);
                $("#selectAll").parent().addClass("checkbox_bg_check");
            }
        } else {
            $(this).parent("i").removeClass('checkbox_bg_check');
            $("#selectAll").prop("checked", false);
            $("#selectAll").parent("i").removeClass('checkbox_bg_check');
        }
    })

    $("#courseList").on("click", "[name='editBtn']", function () {
        var id = $(this).parents("tr").children("td").eq(0).find("input[type='checkbox']").data("id");
        window.location.href = "/backend/course/basic?courseId=" + id;
    })

    //绑定回车
    $("#selectTitle").keydown(function (e) {
        if (e.keyCode == 13) {
            $("#searchBtn").trigger("click");
        }
    })

    Handlebars.registerHelper("subStr", function (size, value) {
        if (value.length <= size) {
            return value;
        }
        var result = value.substring(0, size);
        result += "...";
        return result;
    })
})