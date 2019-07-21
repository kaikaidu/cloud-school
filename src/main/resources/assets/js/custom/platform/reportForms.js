var str = "";//数据拼接
var pageSize = 10,pageNo=1;//每页数量
var name = "",sort="1",label="";//姓名,时间排序，标签
var checkedLength = 0;
$(function(){
    //初始化分页
    initPager(pageNo,pageSize);
    //获取标签
    getLabelAll();

    //点击查询
    $(".ipnBtn").click(function(){
        initPager(pageNo,pageSize);
    });

    //获取排序
    $("#sort").change(function(){
        sort = $(this).val();
        initPager(pageNo,pageSize);
    });

    //获取标签
    $("#label").change(function () {
       label = $(this).val();
        initPager(pageNo,pageSize);
    });

    //绑定checkbox
    $("tbody").on("click","i",function(){
        var checkLength = $(".check").length;//获取checkbox总长度
        if ($(this).children().is(":checked")) {
            $(this).addClass("checkbox_bg_check").children().prop("checked",true);
            checkedLength ++;
        } else {
            $(this).removeClass("checkbox_bg_check").children().prop("checked",false);
            checkedLength --;
        }
        if (checkedLength == checkLength) {
            $("#selectAll").prop("checked",true);
            $("#selectAll").parent().addClass("checkbox_bg_check");
        } else {
            $("#selectAll").prop("checked",false);
            $("#selectAll").parent().removeClass("checkbox_bg_check");
        }
    });
    //全选
    $("#selectAll").click(function(){
        if ($(this).is(":checked")) {
            checkedLength = $(".check").length;
        } else {
            checkedLength = 0;
        }
    });

    //点击导出
    $(".btnA").click(function(){
        $(".winTs,.codeLayerbj").fadeIn();
        var ids = "";
        $(".check").each(function(){
            if ($(this).is(":checked")) {
                ids += $(this).attr("data-id")+",";
            }
        });
        getValue();
        $.ajax({
            url : "/backend/platform/getExportList" ,
            type : "post" ,
            dataType : "json" ,
            data : {"ids":ids,"name":name,"label":label} ,
            beforeSend:function(){
            },
            success : function (res) {
                location.href = "/backend/platform/exportReportForms?ids="+ids+"&name="+name+"&label="+label;
                $('.winTs,.codeLayerbj').fadeOut();
            }
        });
    });
});

//初始化分页插件
function initPager(pageNo,pageSize){
    //调用分页
    laypage.render({
        elem: 'testPager',
        count: getCourseCount(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            courseList(obj.curr,pageSize);
        }
    });
}

//获取参数
function getValue() {
    name = $.trim($("#name").val());
}

//获取课程总数
function getCourseCount() {
    getValue();
    var count = 0;
    $.ajax({
        type: 'post',
        dataType: 'json',
        data : {
            "name":name,"label":label
        },
        url: '/backend/platform/getCourseCount',
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

//查询课程列表
function courseList(pageNo,pageSize) {
    checkedLength = 0;
    $.ajax({
        url : "/backend/platform/courseList" ,
        type : "post" ,
        dataType : "json" ,
        data : {"pageNo":pageNo,"pageSize":pageSize,"name":name,"sort":sort,"label":label} ,
        success : function (res) {
            str = "";
            $(".rev").remove();
            if ("success" == res.message) {
                appendHtml(res.data.list);
            }
        }
    })
}

//数据拼接
function appendHtml(list) {
    if (list.length < 1) {
        $("#testPager").hide();
        $(".padTB20").show();
    } else {
        for (var i = 0; i < list.length; i++) {
            str += '<tr class="rev">';
            str += '<td title="' + list[i].title + '"><i class="input_style checkbox_bg"><input type="checkbox" name="ids" class="check" data-id="' + list[i].id + '"></i>' + (list[i].title.length > 10 ? list[i].title.substr(0, 10) + "..." : list[i].title) + '</td>';
            str += '<td>' + list[i].createTime.substring(0, 10) + '</td>';
            str += '<td>' + list[i].label + '</td>';
            str += '</tr>';
        }
        $("#testPager").show();
        $("#course").after(str);
        $(".padTB20").hide();
    }
}

//获取标签
function getLabelAll() {
    $.ajax({
        url : "/backend/resource/getLabelAll" ,
        type : "post" ,
        dataType : "json" ,
        success : function (res) {
            str = "";
            if ("success" == res.message) {
                var list = res.data;
                for (var i = 0; i < list.length ; i ++) {
                    str += '<option value="'+list[i].id+'">'+list[i].name+'</option>';
                }
            }
            $(".label").after(str);
        }
    })
}