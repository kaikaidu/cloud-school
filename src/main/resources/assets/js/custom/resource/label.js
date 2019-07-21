var str = "";//数据拼接
var pageSize = 10,pageNo=1;//每页数量
var name = "",id="";//标签名称,标签id
var delete_state = -1;//全删标识
var checkedLength = 0 ;
var oldLabelName = "";
$(function(){
    //初始化分页
    initPager(pageNo,pageSize);

    //点击查询
    $(".ipnBtn").click(function(){
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
            delete_state = 1;
        } else {
            $("#selectAll").prop("checked",false);
            $("#selectAll").parent().removeClass("checkbox_bg_check");
            delete_state = -1;
        }
    });
    //全选
    $("#selectAll").click(function(){
        if ($(this).is(":checked")) {
            checkedLength = $(".check").length;
            delete_state = 1;
        } else {
            checkedLength = 0;
            delete_state = -1;
        }
    });

    //显示新增弹出层
    $(".swt_but").click(function(){
        $("#title").html("新增标签");
        $(".addDefine").attr("href","javascript:addLabel();");
        clearValue();
        $(".codeLayer,.codeLayerbj").fadeIn();
    });

    //排序
    $("#sort").change(function(){
        initPager(pageNo,pageSize);
    });

    //多个删除
    $("#delete").click(function(){
        deleteLabel(1);
    });

    //单个删除
    $("tbody").on("click",".labelDel",function(){
        delete_state = -1;
        deleteLabel("",this);
    });

    //显示修改弹出层
    $("tbody").on("click",".labelUp",function(){
        $("#title").html("编辑标签");
        id = $(this).parent().parent().find("input").attr("data-id");
        $(".addDefine").attr("href","javascript:updateLabel();");
        $.ajax({
            url : "/backend/resource/getLabel" ,
            type : "post" ,
            dataType : "json" ,
            data : {"id":id} ,
            success : function (res) {
                if ("success" == res.message) {
                    addStyle();
                    $("#labelName").val(res.data.name);
                    oldLabelName = res.data.name;
                    $(".codeLayer,.codeLayerbj").fadeIn();
                } else {
                    layer.msg(res.message);
                }
            }
        })
    });

    //点击删除提示详情
    $("body").on("click",".remove-tip",function () {
        var labelId = $(this).data("id");
        $.ajax({
            url : "/backend/resource/findCourse" ,
            type : "post" ,
            dataType : "json",
            data : {"labelId":labelId} ,
            success : function (res) {
                if ("success" == res.message) {
                    var source = $("#label-remove-detail-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(res.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'label_remove_detail_show' //设定一个id，防止重复弹出
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
});

//清空
function clearValue() {
    $("#labelName").val("");
}

//初始化分页插件
function initPager(pageNo,pageSize){
    getValue();
    //调用分页
    laypage.render({
        elem: 'testPager',
        count: getLabelCount(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            findLabelByName(obj.curr,pageSize);
        }
    });
}

//查询标签列表
function findLabelByName(pageNo,pageSize) {
    checkedLength = 0;
    $.ajax({
        url : "/backend/resource/findLabelByName" ,
        type : "post" ,
        dataType : "json" ,
        data : {"pageNo":pageNo,"pageSize":pageSize,"name":name,"sort":$("#sort").val()} ,
        success : function (res) {
            //清空全选按钮
            $("#selectAll").parent().removeClass("checkbox_bg_check");
            $("#selectAll").removeAttr("checked");
            str = "";
            $(".rev").remove();
            if ("success" == res.message) {
                appendHtml(res.data.list);
            }
        }
    })
}

//拼接数据
function appendHtml(list){
    if (list.length < 1) {
        $("#testPager").hide();
        $(".padTB20").show();
    } else {
        for (var i = 0;i < list.length ; i++) {
            str += '<tr class="rev">';
            str += '<td><i class="input_style checkbox_bg"><input type="checkbox" name="ids" class="check" value="" data-id="'+list[i].id+'" data-cs="'+list[i].courses+'"></i>'+list[i].name+'</td>';
            str += '<td>'+msecToDate(list[i].createTime).substring(0,10) +'</td>';
            str += '<td class="mar10">';
            if(list[i].courses > 0){
                str += '<a href="javascript:void(0);" class="swt_but Dis" style="color: #c9c9c9; cursor: not-allowed" data-sum="01">编辑</a>';
            }else{
                str += '<a href="javascript:void(0);" class="swt_but labelUp" data-sum="01">编辑</a>';
            }
            str += '<a href="javascript:void(0);" class="labelDel">删除</a></td>';
            str += '</tr>';
        }
        $("#testPager").show();
        $("#label").after(str);
        $(".padTB20").hide();
    }
}

//获取总数
function getLabelCount(){
    var count = 0;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        data : {
            "name":name
        },
        url: '/backend/resource/getLabelCount',
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

function getValue() {
    name = $.trim($("#name").val());
}

//删除时校验是否选中
function deleteLabel(state,i) {
    var ids = "";
    if (state == 1) {
        $(".check").each(function(){
            if ($(this).is(":checked")) {
                ids += $(this).attr("data-id")+",";
            }
        });
        if (ids.length < 1) {
            layer.msg("请选择要删除的标签");
            return;
        }
    } else {
        ids = $(i).parent().parent().find("input").attr("data-id") + ",";
    }
    deleteLabelById(ids);
}


//删除
function deleteLabelById(ids){
    layer.confirm("确认删除吗?",function (index) {
        $.ajax({
            url : "/backend/resource/deleteLabel" ,
            type : "post" ,
            dataType : "json",
            data : {"ids":ids,"state":delete_state} ,
            success : function (res) {
                layer.close(index);
                if ("success" == res.message) {
                    var source = $("#label-remove-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(res.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'label_detail_show' //设定一个id，防止重复弹出
                        ,resize: false
                        ,btn: ['确定']
                        ,btnAlign: 'c'
                        ,content: templateHtml
                        ,yes: function(index, layero){
                            layer.close(index);
                            location.reload(true);
                        }
                    });
                }
            }
        });
    })
}

//新增
function addLabel(){
    //事件移除
    $(".addDefine").attr("href","javascript:void(0);");
    //校验字段
    var flag = checkValue();
    if (flag) {
        $.ajax({
            url : "/backend/resource/addLabel" ,
            type : "post" ,
            dataType : "json" ,
            data : {"name":name} ,
            success : function (res) {
                if ("success" == res.message) {
                    layer.msg("新增成功");
                    $(".codeLayer,.codeLayerbj").fadeOut();
                    //初始化分页
                    initPager(pageNo,pageSize);
                } else {
                    layer.msg(res.message);
                }
            }
        })
    } else {
        $(".addDefine").attr("href","javascript:addLabel();");
    }
}

//校验字段
function checkValue(oldName) {
    name = $.trim($("#labelName").val());
    //非空校验
    if (name.length < 1) {
        layer.msg("请输入标签名字");
        return false;
    }
    if (oldName != name) {
        if (!checkNameOnly(name)) {
            layer.msg("标签名字已存在");
            return false;
        }
    }
    if (name.length > 10) {
        layer.msg("输入的标签名字过长");
        return false;
    }
    return true;
}

//修改
function updateLabel(){
    name = $.trim($("#labelName").val());
    //校验字段
    var flag = checkValue(oldLabelName);
    if (!flag) {
        return;
    }
    $.ajax({
        url : "/backend/resource/updateLabel" ,
        type : "post" ,
        dataType : "json" ,
        data : {"id":id,"name":name} ,
        success : function (res) {
            if ("success" == res.message) {
                layer.msg("修改成功");
                //初始化分页
                initPager(pageNo,pageSize);
                $(".codeLayer,.codeLayerbj").fadeOut();
            } else {
                layer.msg(res.message);
            }
        }
    })
}

//添加样式并且显示弹出框
function addStyle(){
    var thmlH= $(window).height();
    var layerH = $(".addTestQu").height();
    $(".addTestQu").css("top",(thmlH-layerH)/2+"px");
    $(".codeLayer,.codeLayerbj").fadeIn();
}

//校验标签名称是否唯一
function checkNameOnly(name) {
    var flag = false;
    $.ajax({
        url : "/backend/resource/getLabelByName" ,
        type : "post" ,
        dataType : "json" ,
        async : false,
        data : {"id":id,"name":name} ,
        success : function (res) {
            if ("success" == res.message) {
                flag = true;
            }
        }
    })
    return flag;
}