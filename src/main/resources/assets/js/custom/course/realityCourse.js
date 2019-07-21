//线下课程
var str = "";//数据拼接
var pageSize = 10,pageNo=1;//每页数量
var sex=null,name=null,viaState=null,courseId=null;//性别，姓名，通过状态,课程id
var state = "";
var checkedLength = 0;
var t = null;
var signStateLength = 0;
var userIds;
$(function(){
    courseId = $("#courseId").val();//课程id
    //获取当前课程绑定的标签
    getLabelByCourseId(courseId);
    //分页插件初始化
    initPager(pageNo,pageSize);
    //查询进度
    getSuccessCount();

    //关联文件上传元素
    $("#file_upload_button").click(function () {
        $("#file").click();
    });

    //文件上传域改变
    $("#file").change(function () {
        var fileName = $(this).val();
        var pos = fileName.lastIndexOf("\\");
        $("#file_upload_1-queue").html(fileName.substring(pos+1));
        $("#btnB").attr("href","javascript:determineUpload();").removeClass("Dis");
    });

    //点击搜索
    $(".ipnBtn").click(function(){
        initPager(pageNo,pageSize);
    })

    //选择性别，报名状态，通过状态
    $(".stySelS").change(function(){
        initPager(pageNo,pageSize);
    })

    //获取修改的通过状态
    $(".stySel").change(function(){
        state = $(this).val();
    })

    //绑定checkbox
    $("tbody").on("click","i",function(){
        var checkLength = $(".check").length;//获取checkbox总长度
        var sign = $(this).children().attr("data-sign");
        console.log(sign)
        if ($(this).children().is(":checked")) {
            $(this).addClass("checkbox_bg_check").children().prop("checked",true);
            checkedLength ++;
            if (sign == 0 || sign == 1) {
                signStateLength ++;
            }
        } else {
            $(this).removeClass("checkbox_bg_check").children().prop("checked",false);
            checkedLength --;
            if (sign == 0 || sign == 1) {
                signStateLength --;
            }
        }
        if (signStateLength == checkedLength) {
            $("#push").removeClass("Dis");
            $("#push").attr("href","javascript:push();");
        } else {
            $("#push").addClass("Dis");
            $("#push").attr("href","javascript:void(0);");
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
        signStateLength = 0;
        if ($(this).is(":checked")) {
            checkedLength = $(".check").length;
            var checkLength = $(".check").length;//获取checkbox总长度
            $(".check").each(function(){
                if ($(this).attr("data-sign")==0 || $(this).attr("data-sign") == 1) {
                    signStateLength ++;
                }
            });
        } else {
            checkedLength = 0;
            checkLength = 0;
        }
        if (signStateLength == checkLength) {
            $("#push").removeClass("Dis");
            $("#push").attr("href","javascript:push();");
        } else {
            $("#push").addClass("Dis");
            $("#push").attr("href","javascript:void(0);");
        }
    });

    //批量删除
    $("#batchDel").click(function () {
        var ids = "";
        $(".check").each(function(){
            if ($(this).is(":checked")) {
                ids += $(this).attr("data-id") + ",";
            }
        });

        if (ids.length < 1) {
            layer.msg("请选择需要删除的学员!",{icon: 5});
            return;
        }
        deleteById(ids);
    });
});

//删除
function deleteById(ids){
    layer.confirm("确认删除吗?",function (index) {
        userIds = ids;
        $.ajax({
            url : "/backend/course/state/batchDel" ,
            type : "post" ,
            dataType : 'json' ,
            data : {"ids":ids,"courseId":courseId} ,
            success : function (res) {
                layer.close(index);
                var source = $("#reality-remove-show").html();
                var template = Handlebars.compile(source);
                var templateHtml = template(res.data);
                layer.open({
                    type: 1
                    ,title: '提示信息'//不显示标题栏
                    ,closeBtn: 0
                    ,area: '500px;'
                    ,shade: 0.3
                    ,id: 'label_reality_show' //设定一个id，防止重复弹出
                    ,resize: false
                    ,btn: ['确定']
                    ,btnAlign: 'c'
                    ,content: templateHtml
                    ,yes: function(index, layero){
                        layer.close(index);
                        //查询是否已分班
                        var flag = checkClass();
                        //在已分班和删除学员成功后才可提示是否分班
                        if (res.code == "00" && flag) {
                            setTimeout("showWarning()",1000);
                        } else {
                            location.reload(true);
                        }
                    }
                });
            }
        });
    })
}

//查询是否已分班
function checkClass() {
    var flag = false;
    $.ajax({
        type:"post",
        url:"/backend/course/state/checkClass",
        dataType : 'json' ,
        data : {"courseId":courseId} ,
        async : false ,
        success:function(data){
            if(data.code == "00"){
                flag = true;
            }
        }
    });
    return flag;
}

//弹出提示框
function showWarning() {
    var str = "是否需要重新分班?";
    layer.confirm(str, {
        btn: ['是','否'] //按钮
    }, function(){
        //清空分班和抽签
        deleteClassAndDraw(0,userIds,courseId);
    }, function(){
        //剔除删除掉的学员
        deleteClassAndDraw(1,userIds,courseId);
    });
    $(".layui-layer-close1").click(function () {
        if ($(this).parent().prev().html() == str) {
            //剔除删除掉的学员
            deleteClassAndDraw(1,userIds,courseId);
        }
    })
}

function deleteClassAndDraw(type,userIds,courseId) {
    $.ajax({
        url : "/backend/course/state/deleteClassAndDraw" ,
        type : "post" ,
        dataType : 'json' ,
        data : {"ids":userIds,"courseId":courseId,"type":type} ,
        success : function (res) {
            if (res.code == "00") {
                if (type == 0) {
                    location.href = "/backend/course/classSettingChoose?courseId="+courseId;
                } else {
                    location.reload(true);
                }
            } else {
                layer.msg(res.message,{icon: 5});
            }
        }
    });
}


//点击上传 校验数据唯一 插入数据
function determineUpload() {
    var trLength = $(".rev").length;
    $("#hide_courseId").val(courseId);
    $("#hide_dataLength").val(trLength);
    $("#uoload_form").ajaxSubmit({
        url : "/backend/course/checkInsertUser" ,
        type : "post" ,
        dataType : "json" ,
        beforeSend:function(){
            layer.load(2);
        },
        success : function(res){
            //成功后 清空array 删除页面excel文件，按钮，刷新页面数据（条件分页查询带排序）
            if ("00" == res.code) {
                //删除页面excel文件，确认上传按钮置灰禁止点击
                $("#btnB").attr("href","javascript:void(0);").addClass("Dis");
                layer.msg("保存成功！",{icon: 1});
                //分页插件初始化
                initPager(pageNo,pageSize);
            } else {
                layer.msg(res.message,{icon: 5});
            }
            layer.closeAll('loading');
        }
    })
}

//获取成功数量
function getSuccessCount() {
    $.ajax({
        url : "/backend/course/getSuccessCount" ,
        type : "post" ,
        dataType : "json" ,
        data : {"courseId":courseId} ,
        success : function (res) {
            if (null != res.data) {
                $("#successCount").html(res.data);
            }
        }
    })
}

//获取导入进度
// function getCompletedProgress() {
//     $.ajax({
//         url : "/backend/course/getCompletedProgress" ,
//         type : "post" ,
//         dataType : 'json' ,
//         data:{"courseId":courseId},
//         beforeSend:function(){},
//         success : function(res) {
//             if (Number(res.data) >= 99) {
//                 $("#perc").html("99%");
//                 $("#probar").attr("style","width: "+99+"%")
//                 clearTimeout(t);
//             } else {
//                 $("#perc").html(Number(res.data)+"%");
//                 $("#probar").attr("style","width: "+Number(res.data)+"%")
//             }
//         }
//     })
//     t = setTimeout("getCompletedProgress()",2000);
// }



//导出
function exportUser(){
    var ids = "";
    $(".check").each(function(){
        if ($(this).is(":checked")) {
            ids += $(this).attr("data-id") + ",";
        }
    });
    getValue();
    location.href = "/backend/course/exportUser?ids="+ids+"&courseId="+courseId+"&name="+name+"&sex="+sex+"&viaState="+viaState;
}

//点击修改
function updateSignState(){
    var ids = "";
    $(".check").each(function(){
        if ($(this).is(":checked")) {
            ids += $(this).attr("data-id") + ",";
        }
    });
    if (ids.length < 1) {
        layer.msg("请选择需要修改的学员!",{icon: 5});
        return;
    }
    if (state.length < 1) {
        layer.msg("请选择需要修改的状态!",{icon: 5});
        return;
    }
    $.ajax({
        url : "/backend/course/updateSignState" ,
        type : "post" ,
        dataType : 'json' ,
        data : {"ids":ids,"state":state} ,
        success : function(res) {
            if ("success" == res.message) {
                layer.msg("修改成功",{icon: 1});
                initPager(pageNo,pageSize);
            } else {
                layer.msg(res.message,{icon: 5});
            }
        }
    })
}

//异步请求获取学员列表
function selectCourseSignupByCourseId(pageNo,pageSize){
    checkedLength = 0;
    $.ajax({
        url : "/backend/course/selectUserAndSignupByCourseId" ,
        type : "get" ,
        dataType : "json" ,
        data : {
            "courseId":courseId,
            "pageNo": pageNo,
            "pageSize":pageSize,
            "name":name,
            "sex":sex,
            "viaState":viaState
        } ,
        beforeSend :function(xmlHttp){
            //为了清除ie页面缓存
            xmlHttp.setRequestHeader("If-Modified-Since","0");
            xmlHttp.setRequestHeader("Cache-Control","no-cache");
        },
        success : function(res){
            //清空全选按钮
            $("#selectAll").parent().removeClass("checkbox_bg_check");
            $("#selectAll").removeAttr("checked");
            str = "";
            $(".rev").remove();
            if ("success" == res.message && res.data.list.length > 0) {
                $(".clearfix,.tableWrap").show();
                appendHtml(res.data.list);
                $("#testPager").show();
                //查询进度
                getSuccessCount();
            } else {
                $("#testPager").hide();
                $(".padTB20").show();
            }
        }
    })
}

//渲染数据
var appendHtml = function(list) {
    for (var i = 0; i < list.length; i++) {
        str += '<tr class="rev">';
        str += '<td><i class=\'input_style checkbox_bg\'><input type="checkbox" name="ids" class="check" data-id="' + list[i].id + '" data-sign="' + list[i].signState + '"></i>' + list[i].name + '</td>';
        str += '<td>' + list[i].adaNumber + '</td>';
        switch (list[i].sex) {
            case 0 :
                str += '<td>男</td>';
                break;
            case 1:
                str += '<td>女</td>';
                break;
            case 2:
                str += '<td>夫妻</td>';
                break;
            default:
                str += '<td></td>';
        }
        switch (list[i].signState) {
            case 1 :
                str += '<td><span>报名成功</span></td>';
                break;
            case 4:
                str += '<td><span class="ct_green">已完成</span></td>';
                break;
            default:
                str += '<td></td>';
        }
        switch (list[i].viaState) {
            case 0 :
                str += '<td><span class="ct_red">未通过</span></td>';
                break;
            case 1:
                str += '<td><span class="ct_green">已通过</span></td>';
                break;
            default:
                str += '<td></td>';
        }
        str += '</tr>';
    }
    $("#user").after(str);
    $(".padTB20").hide();
}

//初始化分页插件
function initPager(pageNo,pageSize){
    signStateLength = 0;
    getValue();
    //调用分页
    laypage.render({
        elem: 'testPager',
        count: getTotalData(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            selectCourseSignupByCourseId(obj.curr,pageSize);
        }
    });
}


//获取参数
var getValue = function(){
    sex = $("#sex").val();
    name = $(".ipnSearch").val().trim();
    viaState = $("#viaState").val();
}

//第一次查询总数
var getTotalData = function() {
    var count = 0;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        data : {"courseId":courseId,
            "name":name,
            "sex":sex,
            "viaState":viaState
        },
        url: '/backend/course/selectUserSignCount',
        async: false ,
        beforeSend :function(xmlHttp){
            //为了清除ie页面缓存
            xmlHttp.setRequestHeader("If-Modified-Since","0");
            xmlHttp.setRequestHeader("Cache-Control","no-cache");
        },
        success: function(res){
            if(res.message == "success"){
                count = res.data;
            } else {
                layer.msg(res.message,{icon: 5})
            }
        }
    });
    return count;
}

//根据课程id查询标签
function getLabelByCourseId(courseId) {
    $.ajax({
        url : "/backend/course/getLabelByCourseId" ,
        type : "post" ,
        dataType : "json" ,
        data : {"courseId":courseId} ,
        success : function (res) {
            $("#labelName").html(res.data);
        }
    })
}

function close() {
    $(".addTestQu").hide();
}
