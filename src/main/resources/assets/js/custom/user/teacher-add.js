var name = "",email = "",pwd = "";
function addTeacher(){
    //事件移除
    $(".addDefine").attr("href","javascript:void(0);");
    //校验字段
    var flag = checkValue();
    if (flag) {
        $.ajax({
            url : "/backend/user/addTeacher" ,
            type : "post" ,
            dataType : "json" ,
            data : {"name":name,"email":email,"password":pwd} ,
            success : function (res) {
                if ("success" == res.message) {
                    layer.msg("新增成功");
                    location.href = "/backend/user/getTeacher";
                } else {
                    layer.msg(res.message);
                }
            }
        })
    } else {
        $(".addDefine").attr("href","javascript:addTeacher();");
    }
}

//校验字段
function checkValue(oldEmail) {
    name = $.trim($("#name").val());
    email = $.trim($("#email").val());
    pwd = $.trim($("#pwd").val());
    //非空校验
    if (name.length < 1) {
        layer.msg("请输入讲师名字");
        return false;
    }
    if (name.length > 25) {
        layer.msg("输入的讲师名字过长");
        return false;
    }
    if (email.length < 1) {
        layer.msg("请输入电子邮箱");
        return false;
    }
    if (!isEmail(email)) {
        layer.msg("输入的邮箱格式不正确");
        return false;
    }
    //校验邮箱是否唯一
    if (oldEmail != email) {
        if (!checkEmailOnly(email)) {
            layer.msg("输入的邮箱已存在");
            return false;
        }
    }
    if (pwd.length < 1) {
        layer.msg("请输入密码");
        return false;
    }
    if (!isChina(pwd)) {
        layer.msg("密码不能包含中文");
        return false;
    }
    if (pwd.length < 6 || pwd.length > 20) {
        layer.msg("输入的密码长度不正确,密码长度为6-20");
        return false;
    }
    if(checkPass(pwd,email)){
        return false;
    }
    return true;
}

//校验邮箱是否唯一
function checkEmailOnly(email) {
    var flag = false;
    $.ajax({
        url : "/backend/user/getTeacherByEmail" ,
        type : "post" ,
        dataType : "json" ,
        data : {"email":email} ,
        async : false ,
        success : function (res) {
            if ("success" == res.message) {
                flag = true;
            }
        }
    });
    return flag;
}

function checkPass(pwd,email){
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
