var str = "";//数据拼接
var pageSize = 10,pageNo=1;//每页数量
var name = "",id="",phone="",address="";
var provName="",provCode="",cityName="",cityCode="",regionName="",regionCode="";//省名称，省code，城市名称，城市code，区域名称,区域code
var provId = "",cityId="";
var checkedLength = 0 ;
$(function(){
    //分页插件初始化
    initPager(pageNo,pageSize);

    //点击搜索
    $(".ipnBtn").click(function(){
        initPager(pageNo,pageSize);
    })

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

    //点击编辑
    $(".tableWrap").on("click",".swt_but",function(){
        clearValue();
        $(".op_dev,.ci_dev1,.ci_dev2").remove();
        id = $(this).parent().parent().find("input").attr("data-id");
        $.ajax({
            url : "/backend/user/getStudentById" ,
            type : "post" ,
            dataType : "json" ,
            data : {
                "id":id
            } ,
            success : function(res){
                if ("success" == res.message) {
                    var obj = res.data;
                    $(".name").html(obj.name);
                    switch (obj.sex) {
                        case 0:
                            $(".sex").html("男");break;
                        case 1:
                            $(".sex").html("女");break;
                        case 2:
                            $(".sex").html("夫妻");break;
                        default:
                            $(".sex").html("");
                    }

                    $(".adaNumber").html(obj.adaNumber);
                    $("#phone").val(obj.phone);
                    $("#address").val(obj.address);
                    $(".addTestQu,.codeLayerbj").fadeIn();
                    //获取省份
                    provinceList();
                    if (null != obj.cityCode && obj.cityCode.length > 0) {
                        //根据省份获取城市
                        cityList(obj.provCode);
                        //根据城市获取区域
                        regionList(obj.cityCode);
                    }
                    provCityReg(obj);
                }
            }
        })
    });

    //点击修改保存
    $(".addDefine").click(function(){
        getValue();
        //校验值
        var flag = checkValue();
        if (!flag) {
            return;
        }
        $.ajax({
            url : "/backend/user/updateStudent" ,
            type : "post" ,
            dataType : "json" ,
            data : {
                "id":id,
                "phone":phone,
                "address":address,
                "province":provName == '请选择' ? '' : provName,
                "provCode":provCode,
                "city":cityName == '请选择' ? '' : cityName,
                "cityCode":cityCode,
                "region":regionName == '请选择' ? '' : regionName,
                "regionCode":regionCode
            } ,
            success : function(res){
                if ("success" == res.message) {
                    layer.msg("修改成功");
                    initPager(pageNo,pageSize);
                    $("#phone,#address").val("");
                    $(".addTestQu,.codeLayerbj").fadeOut();
                } else {
                    layer.msg(res.message);
                }
            }
        })
    });
    
    //省域改变
    $(".province").change(function () {
        provName = $(".province option:selected").text()
        provCode = $(this).val();
        cityList(provCode);
        regionList(cityId);
    });

    //城市域改变
    $(".city").change(function () {
        cityName = $(".city option:selected").text()
        cityCode = $(this).val();
        regionList(cityCode);
    });

    //区域改变
    $(".region").change(function () {
        regionName = $(".region option:selected").text()
        regionCode = $(this).val();
    })
});

//校验字段
function checkValue() {
    if (phone.length > 1) {
        if (!isPhone(phone)) {
            layer.msg("请输入正确格式的手机号");
            return false;
        }
    }
    if (address.length > 1) {
        if (address.length > 120) {
            layer.msg("详细地址输入过长，不能超过120字符")
            return false;
        }
    }
    return true;
}

//填充省市区
function provCityReg(obj) {
    if (null != obj.provCode && obj.provCode.length > 0) {
        $(".province option").each(function () {
            if ($(this).val() == obj.provCode) {
                $(this).attr("selected","selected");
                cityList(obj.provCode);
            }
        });
    }
    if (null != obj.cityCode && obj.cityCode.length > 0) {
        $(".city option").each(function () {
            if ($(this).val() == obj.cityCode) {
                $(this).attr("selected","selected");
                regionList(obj.cityCode);
            }
        });
    }
    if (null != obj.regionCode && obj.regionCode.length > 0) {
        $(".region option").each(function () {
            if ($(this).val() == obj.regionCode) {
                $(this).attr("selected","selected");
            }
        });
    }
}

//异步请求获取学员列表
function studentList(pageNo,pageSize){
    checkedLength = 0;
    $.ajax({
        url : "/backend/user/studentList" ,
        type : "post" ,
        dataType : "json" ,
        data : {
            "pageNo":pageNo,
            "pageSize":pageSize,
            "name":name
        } ,
        success : function(res){
            //清空全选按钮
            $("#selectAll").parent().removeClass("checkbox_bg_check");
            $("#selectAll").removeAttr("checked");
            str = "";
            if ("success" == res.message) {
                $(".rev").remove();
                appendHtml(res.data.list);
            }

        }
    })
}

//拼接数据
function appendHtml(list) {
    if (list.length < 1) {
        $("#testPager").hide();
        $(".padTB20").show();
    } else {
        var address = "";
        for (var i = 0; i < list.length; i++) {
            str += '<tr class="rev">';
            str += '<td><span class="w10 courseM-td" title="' + list[i].name + '"><i class="input_style checkbox_bg"><input type="checkbox" name="ids" class="check" value="" data-id="' + list[i].id + '"></i>' + list[i].name + '</span></td>';
            str += '<td>' + list[i].adaNumber + '</td>';
            switch (list[i].sex) {
                case 0:
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
            str += '<td>' + (list[i].phone == null ? '' : list[i].phone) + '</td>';
            address = (list[i].province == null ? '' : list[i].province + " ") + (list[i].city == null ? '' : list[i].city + " ") + (list[i].region == null ? '' : list[i].region + " ") + (list[i].address == null ? '' : list[i].address);
            if (address.length > 1) {
                str += '<td title="' + address + '">' + (address.length < 15 ? address : address.substr(0, 15) + "...") + '</td>';
            } else {
                str += '<td></td>>';
            }
            str += '<td><a href="javascript:void(0);" class="swt_but" data-sum="25">编辑</a></td>';
            str += '</tr>';
        }
        $("#testPager").show();
        $("#user").after(str);
        $(".padTB20").hide();
    }
}


//初始化分页插件
function initPager(pageNo,pageSize){
    name = $.trim($("#name").val());
    console.log(name);
    //调用分页
    laypage.render({
        elem: 'testPager',
        count: getTotalData(),
        limit: pageSize,
        theme: '#1962a9',
        curr: pageNo,
        jump: function(obj, first){
            studentList(obj.curr,pageSize);
        }
    });
}

//第一次查询总数
var getTotalData = function() {
    var count = 0;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        data : {
            "name":name,
        },
        url: '/backend/user/getStudentCount',
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
    phone = $.trim($("#phone").val());
    address = $.trim($("#address").val());
    provName = $(".province option:selected").text();
    provCode =$(".province option:selected").val();
    cityName = $(".city option:selected").text();
    cityCode = $(".city option:selected").val();
    regionName = $(".region option:selected").text();
    regionCode = $(".region option:selected").val();
}

//删除
function deleteTeacher() {
    var ids = "";
    $(".check").each(function(){
        if ($(this).is(":checked")) {
            ids += $(this).attr("data-id")+",";
        }
    });
    if (ids.length < 1) {
        layer.msg("请选择要删除的学员");
        return;
    }
    layer.confirm("确认删除吗?",function () {
        $.ajax({
            url : "/backend/user/updateStudentById" ,
            type : "post" ,
            dataType : "json",
            data : {"ids":ids} ,
            success : function (data) {
                if(data.code == '00'){
                    var source = $("#student-remove-show").html();
                    var template = Handlebars.compile(source);
                    var templateHtml = template(data.data);
                    layer.open({
                        type: 1
                        ,title: '提示信息'//不显示标题栏
                        ,closeBtn: 0
                        ,area: '500px;'
                        ,shade: 0.3
                        ,id: 'student_remove_show' //设定一个id，防止重复弹出
                        ,resize: false
                        ,btn: ['确定']
                        ,btnAlign: 'c'
                        ,content: templateHtml
                        ,yes: function(index, layero){
                            location.reload(true);
                        }
                    });
                }else {
                    var msg = data.message?data.message:"删除学员失败！";
                    layer.msg(msg,{icon:5});
                }
            }
        });
    })
}

//获取省份
function provinceList() {
    $.ajax({
        url : "/backend/user/provinceList" ,
        type : "post" ,
        dataType : "json",
        async : false,
        success : function (res) {
            str = "";
            if ("success" == res.message) {
                var list = res.data;
                for (var i = 0 ; i < list.length ; i ++) {
                    str += '<option value="'+list[i].code+'" class="op_dev">'+list[i].name+'</option>';
                }
            } else {
                layer.msg(res.message);
            }
            $("#province").after(str);
        }
    });
}

//根据省份id获取城市
function cityList(code) {
    $(".ci_dev1").remove();
    getAddr(code,1);
    $("#city").after(str);
}

//根据城市id获取区域
function regionList(code) {
    $(".ci_dev2").remove();
    getAddr(code,2);
    $("#region").after(str);
}

//根据code查询市，区
function getAddr(code,state) {
    $.ajax({
        url : "/backend/user/getAddr" ,
        type : "post" ,
        dataType : "json",
        data : {"code":code},
        async : false,
        success : function (res) {
            str = "";
            if ("success" == res.message) {
                var list = res.data;
                for (var i = 0 ; i < list.length ; i ++) {
                    str += '<option value="'+list[i].code+'" class="ci_dev'+state+'">'+list[i].name+'</option>';
                }
                if (list.length > 0) {cityId = list[0].code;}
            } else {
                layer.msg(res.message);
            }
        }
    });
}

//清空数据
function clearValue() {
    $("#phone,#address").val("");
}