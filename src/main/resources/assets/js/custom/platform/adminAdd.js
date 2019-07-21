$(function () {
    //新增弹出层
    $("#add").on("click", function () {
        $(".codeLayerbj,.addTestQu").fadeIn();
    })

    //保存
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
        //数据校验
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
                    window.location.href="/backend/platform/adminIndex"
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
        var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if (!reg.test(param.email)) {
            layer.msg('邮箱格式不正确，请重新填写!',{icon: 5});
            return false;
        }
        if(checkPass(param.password)){
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
        if(pwd.indexOf(email.substr(0,email.indexOf('@')))>0){
            layer.msg('密码不能包含邮箱相关信息!',{icon: 5});
            return true;
        }
        return false;
    }
})