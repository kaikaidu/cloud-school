$(function () {
    var pageNo = 1;
    var pageSize = 10;
    window.onload = function () {
        initadminList();
    }

    function initadminList() {
        //调用分页
        laypage.render({
            elem: 'adminListPager',
            count: getTotalCount(),
            limit: pageSize,
            theme: '#1962a9',
            jump: function(obj, first){
                findadminList(obj.curr, pageSize);
            }
        });
    }

    function findadminList(pageNo, pageSize) {
        var name = $(".ipnSearch").val();
        var ident = $(".stySelS").val();
        $.ajax({
            type: 'POST',
            url: "/backend/platform/adminList",
            data: {
                name:name,
                ident: ident,
                pageNo: pageNo,
                pageSize: pageSize
            },
            dataType: "json",
            success: function (data) {
                if(data.code == "00") {
                    $("#adminListPager").show();
                    $(".no-content").remove();
                    //请求成功处理函数
                    var myTemplate = Handlebars.compile($("#adminList").html());
                    $("tbody tr").eq(0).siblings().remove();
                    $("tbody").append(myTemplate(data.data));
                    if(data.data.length == 0){
                        $(".tableBox").after($("#adminList-no-content").html());
                        $("#adminListPager").hide();
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
        var ident = $(".stySelS").val();
        var count = 0;
        $.ajax({
            type: 'POST',
            url: "/backend/platform/queryCount",
            data: {
                name:name,
                ident: ident,
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

    //搜索按钮事件
    $(".ipnBtn").on("click", function () {
        $("[name='select']").val("");
        initadminList();
    })

    //监听管理员级别
    $("[name='select']").change(function () {
        initadminList();
    })

    //编辑弹出层
    $("tbody").on("click", ".swt_but", function () {
        $("#toptitVal").html("编辑管理员");
        var id = $(this).parent().parent().find("input").val();
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/backend/platform/selectEditAdminData',
            data:{
                uid: id
            },
            success:function(data){
                if(data.message == "success"){
                    $(".codeLayerbj,.addTestQu").fadeIn();
                    $("#uid").val(data.data.id);
                    $("#mySelect").val(data.data.ident);
                    $("#name").val(data.data.name);
                    $("#email").val(data.data.email);
                    $("#password").val(data.data.password);
                }
            },
        });
    })

    //解锁
    $("tbody").on("click", ".deblocking", function () {
        var email = $(this).parent().parent().find(".email").html();
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/backend/platform/deblocking',
            data:{
                email: email
            },
            success:function(data){
                if(data.message == "success"){
                    initadminList();
                    layer.msg("解锁成功",{icon: 1});
                }
            },
        });
    })

    //新增弹出层
    $("#add").on("click", function () {
        $("#toptitVal").html("新增管理员");
        $("#uid").val("");
        $("#name").val("");
        $("#mySelect").val("");
        $("#email").val("");
        $("#password").val("");
        $(".codeLayerbj,.addTestQu").fadeIn();
    })

    //保存数据
    $(".addDefine").on("click",function () {
        var id = $("#uid").val();
        var name = $("#name").val();
        var ident = $("#mySelect").val();
        var email = $("#email").val();
        var password = $("#password").val();
        var param = {};
        param.name = name;
        param.ident = ident;
        param.email = email;
        param.password = password;

        if(!isNUll(param)){
            return;
        }

        $.ajax({
            type: 'POST',
            url: "/backend/platform/editOrAddAdmin",
            data: {
                id:id,
                name: name,
                ident:ident,
                email: email,
                password: password
            },
            dataType: "json",
            success: function (data) {
                if(data.code == '00'){
                    layer.msg("保存成功",{icon: 1});
                    $(".close").trigger("click");
                    initadminList();
                }else {
                    layer.msg(data.message);
                }
            },
            error: function () {
                layer.msg("保存失败",{icon: 5});
            }
        });
    })

    function isNUll(param) {
        if (isEmpty(param.name)) {
            layer.msg("请填写姓名!",{icon: 5});
            return false;
        }
        if (isEmpty(param.ident)) {
            layer.msg("请选择管理员级别!",{icon: 5});
            return false;
        }
        if (isEmpty(param.email)) {
            layer.msg("请填写邮箱!",{icon: 5});
            return false;
        }
        if (isEmpty(param.password)) {
            layer.msg("请填写密码!",{icon: 5});
            return false;
        }
        var reg = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
        if (!reg.test(param.email)) {
            layer.msg('邮箱格式不正确，请重新填写!',{icon: 5});
            return false;
        }
        if(checkPass(param.password,param.email)){
            return false;
        }
        return true;
    }

    function checkPass(pwd,email){
        if(pwd.length < 6){
            layer.msg('密码长度至少6位!',{icon: 5});
            return true;
        }
        if(pwd.length > 18){
            layer.msg('密码长度过长!',{icon: 5});
            return true;
        }
        var ls = 0;
        if(pwd.match(/([a-z])+/)){
            ls++;
        }
        if(pwd.match(/([0-9])+/)){
            ls++;
        }
        if(pwd.match(/([A-Z])+/)){
            ls++;
        }
        if(pwd.match(/[^a-zA-Z0-9]+/)){
            ls++;
        }
        if(ls < 3){
            layer.msg('至少含有大、小写字母、特殊字符和阿拉伯数字中的3种!',{icon: 5});
            return true;
        }
        if(pwd == email){
            layer.msg('邮箱和密码不能相同!',{icon: 5});
            return true;
        }
        if(pwd.indexOf(email.substr(0,email.indexOf('@')))>=0){
            layer.msg('密码不能包含邮箱相关信息!',{icon: 5});
            return true;
        }
        return false;
    }

    //删除
    $("#del").on("click",function () {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        if($selected.length == 0){
            layer.msg("请选择学员!",{icon:5})
            return;
        }
        layer.confirm("确认删除吗?",function () {
            $.ajax({
                type: 'POST',
                url: "/backend/platform/delBatch",
                data: {
                    uids: getCheckedSitemIds()
                },
                dataType: "json",
                async: false,
                success: function (data) {
                    if(data.code == '00'){
                        layer.msg("删除成功", {icon: 1});
                        initadminList();
                    }else {
                        layer.msg(data.message, {icon: 5})
                    }

                },
                error: function () {
                    layer.msg("删除失败", {icom: 5});
                }
            });
        })
    })

    //获取选中的id
    function getCheckedSitemIds() {
        var $selected = $("tbody").find("input[type='checkbox']:checked");
        var uids = "";
        $selected.each(function () {
            var uid = $(this).val();
            uids = uids + uid + ','
        })
        return uids;
    }

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

    $("#selectAll").click(function() {
        $(":checkbox[name='id']").prop("checked", this.checked);
    });

    Handlebars.registerHelper('compare',function(value,options){
        if(value == 0){
            return options.fn(this);
        }else{
            return options.inverse(this);
        }
    });
})
