$(function () {
    var picture = "";
    var qrCode = "";

    window.onload = function(){
        courseDetail();
    }

    $("#courseLabel").change(function () {
        customInput();
    })

    //输入框控制
    function customInput() {
        var label = $("#courseLabel").val();
        if(label == 1 || label == 2){
            $("[name='live']").attr("readonly","readonly").attr("disabled","disabled");
            $("[name='offline']").removeAttr("readonly").removeAttr("disabled");
            $(".jeinput[name='live']").css({"background-color":"#eaeae4"});
        }
        else if(label == 3){
            $("[name='offline']").attr("readonly","readonly").attr("disabled","disabled");
            $("[name='live']").removeAttr("readonly").removeAttr("disabled").css({"background-color":""});
            $(".jeinput[name='live']").css({"background-color":"transparent"});
        } else {
            // $("[name='offline']").removeAttr("readonly").removeAttr("disabled");
            // $("[name='live']").removeAttr("readonly").removeAttr("disabled");
            // $(".jeinput[name='live']").css({"background-color":"transparent"});
            $("[name='live']").attr("readonly","readonly").attr("disabled","disabled");
            $("[name='offline']").removeAttr("readonly").removeAttr("disabled");
            $(".jeinput[name='live']").css({"background-color":"#eaeae4"});
        }
        $('.selectpicker').selectpicker('refresh');
    }

    /*function upload(id,type,fileType) {
        $("#"+id).Huploadify({
            auto:false,
            fileTypeExts:fileType,
            formData:{directory:"img"},
            uploader:"/backend/upload",
            multi:false,
            showUploadedSize:true,
            // removeTimeout:9999999,
            onUploadSuccess:function (file,data) {
                var Data = JSON.parse(data);
                if(Data.code == '00'){
                    if(type==1){
                        picture = Data.data;
                    }
                    else if(type == 2){
                        qrCode = Data.data;
                    }
                    layer.msg("文件上传成功!",{icon:1});
                }else {
                    layer.msg(Data.message,{icon:5});
                }
            },
            onUploadError:function (file,response) {
                layer.msg("文件上传失败",{icon:5});
            },
            onCancel:function (file) {
                if(type==1){
                    picture = "";
                }
                else if(type == 2){
                    qrCode = "";
                }
            }
        })
    }

    upload("courseImg",1,"*.jpg;*.gif;*.png");
    upload("courseQrCode",2,"*.bmp;*.gif;*.jpg;*.png;*.jpeg");*/

    // 上传课程配图
    layupload.render({
        elem: "#courseImg",
        url: "/backend/upload",
        data: {
            directory:"img"
        },
        accept: 'images',// 图片
        exts: 'jpg|gif|png',
        size: 5 * 1024,
        auto: false,
        bindAction: $(".courseImg-preview").find('.uploadbtn'),
        choose: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $(".courseImg-preview").find('img').attr("src", result);//图片链接(base64)
            });
            $("#courseImg").text("上传图片");
            $(".courseImg-preview").css("display", "none").find('.uploadbtn').text("上传");
            // 将picture置为空
            picture = '';
            // todo 重置input file

            $(".courseImg-preview").css("display", "block");
            $(".courseImg-preview").on("click", ".delfilebtn", function () {
                $("#courseImg").text("上传图片");
                $(".courseImg-preview").css("display", "none").find('.uploadbtn').text("上传");
                // 将picture置为空
                picture = '';
                // todo 重置input file
            });
        },
        before: function (obj) {
            layer.load(2);
        },
        done: function(res){
            layer.closeAll("loading");
            if(res.code == '00'){
                picture = res.data;
                layer.msg("文件上传成功!",{icon:1});
                $(".courseImg-preview").find('.uploadbtn').text("已上传");
                $("#courseImg").text("编辑");
            }else {
                layer.msg(res.message,{icon:5});
            }
        },
        error: function () {
            layer.closeAll("loading");
            layer.msg("文件上传失败",{icon:5});
        }
    });

    // 上传课程二维码
    layupload.render({
        elem: "#courseQrCode",
        url: "/backend/upload",
        data: {
            directory:"img"
        },
        accept: 'images',// 图片
        exts: 'bmp|jpg|gif|png|jpeg',
        size: 5 * 1024,
        auto: false,
        bindAction: $(".courseQrCode-preview").find('.uploadbtn'),
        choose: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $(".courseQrCode-preview").find('img').attr("src", result);//图片链接(base64)
            });
            $("#courseQrCode").text("上传二维码");
            $(".courseQrCode-preview").css("display", "none").find('.uploadbtn').text("上传");
            // 将qrCode置为空
            qrCode = '';
            // todo 重置input file

            $(".courseQrCode-preview").css("display", "block");
            $(".courseQrCode-preview").on("click", ".delfilebtn", function () {
                $("#courseQrCode").text("上传二维码");
                $(".courseQrCode-preview").css("display", "none").find('.uploadbtn').text("上传");
                // 将courseQrCode置为空
                qrCode = '';
                // todo 重置input file
            });
        },
        before: function (obj) {
            layer.load(2);
        },
        done: function(res){
            layer.closeAll("loading");
            if(res.code == '00'){
                qrCode = res.data;
                layer.msg("文件上传成功!",{icon:1});
                $(".courseQrCode-preview").find('.uploadbtn').text("已上传");
                $("#courseQrCode").text("编辑");
            }else {
                layer.msg(res.message,{icon:5});
            }
        },
        error: function () {
            layer.closeAll("loading");
            layer.msg("文件上传失败",{icon:5});
        }
    });

    $("#courseSave").click(function () {
        var course = paramCheck();
        if(course == undefined){
            return false;
        }
        var isAppr = $("#courseIsVerify").val();
        var hideIsAppr = $("#hide_isVerify").val();
        var flag = true;
        if (hideIsAppr != isAppr) {
            //校验当前课程是否在导入报名学员
            flag = checkSignImport(course);
        }
        if (hideIsAppr != isAppr && isAppr == 0) {
            //判断是否有报名学员
            var sign = checkSign(course);
            //只有在该课程未操作导入学员和有学员报名的情况下才可提示
            if (flag && sign) {
                layer.confirm('不需要审核后，原审核失败的学员将被删除！', {
                    btn: ['确认','取消'] //按钮
                }, function(){
                    updateCourse(course);
                }, function(){
                    //点击取消
                });
            } else {
                updateCourse(course);
            }
            $("#hide_isVerify").val(isAppr);
        } else {
            if (flag) {
                $("#hide_isVerify").val(isAppr);
                updateCourse(course);
            }
        }
    });

    //查询是否有报名学员
    function checkSign(course) {
        var flag = false;
        $.ajax({
            type:"post",
            url:"/backend/course/checkSign",
            contentType: "application/json",
            data:JSON.stringify(course),
            async : false ,
            success:function(data){
                if(data.code == "00"){
                    flag = true;
                }
            }
        });
        return flag;
    }

    //校验当前课程是否在导入报名学员
    function checkSignImport(course) {
        var flag = false;
        $.ajax({
            type:"post",
            url:"/backend/course/checkSignImport",
            contentType: "application/json",
            data:JSON.stringify(course),
            async : false ,
            success:function(data){
                if(data.code == "00"){
                    flag = true;
                } else {
                    layer.msg(data.message,{icon:5});
                }
            }
        });
        return flag;
    }

    function updateCourse(course) {
        $.ajax({
            type:"post",
            url:"/backend/course/saveBasicInfo",
            contentType: "application/json",
            data:JSON.stringify(course),
            success:function(data){
                if(data.code == "00"){
                    if(!isNaN(data.data)){
                        $("#courseId").val(data.data);
                        layer.msg("保存成功",{icon:1});
                        $("#courseLabel").attr("readonly","readonly").attr("disabled","disabled").css({"background-color":"#eaeae4"});
                        $('.selectpicker').selectpicker('refresh');
                    }else {
                        layer.msg(data.message,{icon:5});
                    }
                } else {
                    layer.msg(data.message,{icon:5});
                }
            }
        })
    }

    function jumpFn(ele) {
        $('html, body').animate({
            scrollTop: $(ele).offset().top
        }, 1000);
        return false;
    }

    function paramCheck(){
        function checkNotNull(param) {
            if(param==null || param==""){
                return false;
            }
            return true;
        }
        var title = $("#courseName").val();
        if(!checkNotNull(title)){
            jumpFn("div.div-courseName");
            layer.msg("课程名称为必填项",{icon:5});
            return;
        }
        if(title.length>50){
            jumpFn("div.div-courseName");
            layer.msg("课程名称超长",{icon:5});
            return;
        }
        var description = $("#courseDesc").val();
        if(!checkNotNull(description)){
            jumpFn("div.div-courseDesc");
            layer.msg("课程描述为必填项",{icon:5});
            return;
        }
        if(description.length>500){
            jumpFn("div.div-courseDesc");
            layer.msg("课程描述超长",{icon:5});
            return;
        }
        var startTime = $("#test03_1").val();
        if(!checkNotNull(startTime)){
            jumpFn("div.div-startTime");
            layer.msg("上课开始时间为必填项",{icon:5});
            return;
        }
        var endTime = $("#test03_2").val();
        if(!checkNotNull(endTime)){
            jumpFn("div.div-endTime");
            layer.msg("上课结束时间为必填项",{icon:5});
            return;
        }
        if(startTime>endTime){
            jumpFn("div.div-startTime");
            layer.msg("上课开始时间大于结束时间",{icon:5});
            return;
        }
        var label = $("#courseLabel").val();
        if(!checkNotNull(label)){
            jumpFn("div.div-courseLabel");
            layer.msg("课程标签为必填项",{icon:5});
            return;
        }
        var code = $("#courseCode").val();
        if(!checkNotNull(code)){
            jumpFn("div.div-courseCode");
            layer.msg("课程代码不能为空",{icon:5});
            return;
        }
        // var teachers = $("#courseTeachers").val();
        // if(!checkNotNull(teachers)){
        //     jumpFn("div.div-courseTeachers");
        //     layer.msg("讲师名单不能为空",{icon:5});
        //     return;
        // }
        // var assists = $("#courseAssists").val();
        // if(!checkNotNull(assists)){
        //     jumpFn("div.div-courseAssists");
        //     layer.msg("助教名单不能为空",{icon:5});
        //     return;
        // }
        var isShare = $("#courseIsShare").val();
        if(!checkNotNull(isShare)){
            jumpFn("div.div-courseIsShare");
            layer.msg("是否允许分享不能为空",{icon:5});
            return;
        }
        var address = $("#courseAddr").val();
        var signStartTime = $("#test03_3").val();
        var signEndTime = $("#test03_4").val();
        var isAppr = $("#courseIsVerify").val();
        var liveUrl = $("#courseUrl").val();
        var maxApplyNum = $("#maxApplyNum").val();
        //线下课程，必修课程
        if(label==1 || label==2){
            if(!checkNotNull(address)){
                jumpFn("div.div-courseAddr");
                layer.msg("上课地点不能为空",{icon:5});
                return;
            }
            var course = {
                courseId      : $("#courseId").val(),
                title         : $("#courseName").val(),
                description   : $("#courseDesc").val(),
                picture       : picture,
                startTime     : $("#test03_1").val(),
                endTime       : $("#test03_2").val(),
                label         : $("#courseLabel").val(),
                code          : $("#courseCode").val(),
                teachers      : $("#courseTeachers").val(),
                assists       : $("#courseAssists").val(),
                address       : $("#courseAddr").val(),
                qrCode        : qrCode,
                isShare       : $("#courseIsShare").val()
            }
            return course;
        }
        //直播课程
        else if(label==3){
            if(!checkNotNull(signStartTime)||!checkNotNull(signEndTime)){
                jumpFn("div.div-applyTime");
                layer.msg("报名时间不能为空",{icon:5});
                return;
            }

            // 新修改设置报名结束时间不能大于课程结束时间
            if(signEndTime>endTime){
                jumpFn("div.div-applyTime");
                layer.msg("报名结束时间大于课程结束时间",{icon:5});
                return;
            }

            if(!checkNotNull(maxApplyNum)){
                jumpFn("div.div-maxApplyNum");
                layer.msg("报名人数限制不能为空",{icon:5});
                return;
            }
            if(!checkNotNull(isAppr)){
                jumpFn("div.div-courseIsVerify");
                layer.msg("是否需要审批不能为空",{icon:5});
                return;
            }
            if(!checkNotNull(liveUrl)){
                jumpFn("div.div-courseUrl");
                layer.msg("课程url不能为空",{icon:5});
                return;
            }
            if(liveUrl.indexOf("https://") == -1){
                jumpFn("div.div-courseUrl");
                layer.msg("课程url请以https://协议地址为标准",{icon:5});
                return;
            }

            var course = {
                courseId      : $("#courseId").val(),
                title         : $("#courseName").val(),
                description   : $("#courseDesc").val(),
                picture       : picture,
                startTime     : $("#test03_1").val(),
                endTime       : $("#test03_2").val(),
                label         : $("#courseLabel").val(),
                code          : $("#courseCode").val(),
                teachers      : $("#courseTeachers").val(),
                assists       : $("#courseAssists").val(),
                qrCode        : qrCode,
                isShare       : $("#courseIsShare").val(),
                signStartTime : $("#test03_3").val(),
                signEndTime   : $("#test03_4").val(),
                isAppr        : $("#courseIsVerify").val(),
                liveUrl       : $("#courseUrl").val(),
                maxApplyNum   : $("#maxApplyNum").val()
            }
            return course;
        }
        else {
            if(signStartTime>signEndTime){
                jumpFn("div.div-applyTime");
                layer.msg("报名开始时间大于报名结束时间",{icon:5});
                return;
            }
            var course = {
                courseId      : $("#courseId").val(),
                title         : $("#courseName").val(),
                description   : $("#courseDesc").val(),
                picture       : picture,
                startTime     : $("#test03_1").val(),
                endTime       : $("#test03_2").val(),
                label         : $("#courseLabel").val(),
                code          : $("#courseCode").val(),
                teachers      : $("#courseTeachers").val(),
                assists       : $("#courseAssists").val(),
                address       : $("#courseAddr").val(),
                qrCode        : qrCode,
                isShare       : $("#courseIsShare").val(),
                signStartTime : $("#test03_3").val(),
                signEndTime   : $("#test03_4").val(),
                liveUrl       : $("#courseUrl").val(),
                maxApplyNum   : $("#maxApplyNum").val()
            }
            return course;
        }
    }

    function courseDetail() {
        var courseId = $("#courseManage").data("id");
        if(courseId==null||courseId==""||courseId==undefined){
            customInput();
            $("#createCourse").show();
            //如果是创建新的课程 则需要先制空label选择
            $('#courseLabel').val("");
            //这句话很重要 需要让selectpicker自己重新渲染ui!!
            $('#courseLabel').selectpicker('render');
            return;
        }
        $("#createCourse").hide();
        $.ajax({
            type:"get",
            cache:false,
            url:"/backend/course/courseInfo",
            data:{
                courseId:courseId
            },
            dataType:"json",
            success:function (data) {
                if(data.code==00){
                    assignCourse(data.data);
                    customInput();
                }else {
                    layer.msg(data.message,{icon:5});
                }
            },
            error:function () {
                layer.msg("系统异常",{icon:5});
            }
        })
    }

    function assignCourse(course) {
        // 编辑课程配图
        if(course.picture){
            picture = course.picture;
            // 页面渲染
            $(".courseImg-preview").find('img').attr("src", picture);
            $(".courseImg-preview").css("display", "block").find('.uploadbtn').text("已上传");
            $("#courseImg").text("编辑");
            // 添加删除事件
            $(".courseImg-preview").on("click", ".delfilebtn", function () {
                $("#courseImg").text("上传图片");
                $(".courseImg-preview").css("display", "none").find('.uploadbtn').text("上传");
                // 将picture置为空
                picture = '';
                // todo 重置input file
            });
        }

        // 编辑二维码
        if(course.qrCode){
            qrCode = course.qrCode;
            // 页面渲染
            $(".courseQrCode-preview").find('img').attr("src", qrCode);
            $(".courseQrCode-preview").css("display", "block").find('.uploadbtn').text("已上传");
            $("#courseQrCode").text("编辑");
            // 添加删除事件
            $(".courseQrCode-preview").on("click", ".delfilebtn", function () {
                $("#courseQrCode").text("上传二维码");
                $(".courseQrCode-preview").css("display", "none").find('.uploadbtn').text("上传");
                // 将courseQrCode置为空
                qrCode = '';
                // todo 重置input file
            });
        }
        // picture = course.picture?course.picture:'';
        // qrCode = course.qrCode?course.qrCode:'';
        $("#courseName").val(course.title);
        $("#courseDesc").val(course.description);
        $("#test03_1").val(course.startTime);
        $("#test03_2").val(course.endTime);
        $("#courseLabel").selectpicker('val',course.label);
        $("#courseCode").val(course.code);
        $("#courseTeachers").selectpicker('val',course.teachers);
        $("#courseAssists").selectpicker('val',course.assists);
        $("#courseAddr").val(course.address);
        $("#courseIsShare").val(course.isShare);
        $("#test03_3").val(course.signStartTime);
        $("#test03_4").val(course.signEndTime);
        $("#courseIsVerify").selectpicker('val',course.isAppr);
        $("#hide_isVerify").val(course.isAppr);
        $("#courseUrl").val(course.liveUrl);
        $('.selectpicker').selectpicker('refresh');
        $("#maxApplyNum").val(course.applyMaxNum);
        if(course.started){
            $("#test03_1").attr("readonly","readonly").attr("disabled","disabled").css({"background-color":"#eaeae4"});
            $("#test03_2").attr("readonly","readonly").attr("disabled","disabled").css({"background-color":"#eaeae4"});
        }
        $("#courseLabel").attr("readonly","readonly").attr("disabled","disabled").css({"background-color":"#eaeae4"});
    }
    
    $("#courseCancel").click(function () {
        layer.confirm("是否确定取消",function () {
            location.replace("/backend/course/courseManage");
        })
    })
})
