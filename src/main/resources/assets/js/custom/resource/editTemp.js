$(function () {
    var sitemTempType = $('#sitemTempType').val();
    if(sitemTempType == 'copy'){
        $("#tempName").select();
        layer.tips('请重新命名该模板名称！', '#tempName', {
            tips: [1, '#3595CC'],
            time: 4000
        });
    }

    //加载初始数据
    findSitemList();

    function findSitemList() {
        var sitemTempId = $("#sitemTempId").val();
        $.ajax({
            type: 'POST',
            url: "/backend/resource/sitemTemp/querySitemData",
            data: {
                sitemTempId: sitemTempId,
            },
            dataType: "json",
            success: function (data) {
                //请求成功处理函数
                var myTemplate = Handlebars.compile($("#sitemList").html());
                $("pf_com div").eq(0).siblings().remove();
                $(".pf_com").append(myTemplate(data.data));

                resetOrder();
            },
            error: function () {
            }
        });
    }

    // 点击弹层的关闭按钮时str
    $(".close,.but_none").click(function(){
        $(this).parent().parent(".codeLayer").fadeOut();
        $(".codeLayerbj").fadeOut();
        // 恢复初始值
        var $parent =$(this).parents(".codeLayer");
        $parent.find("input[type=text]").val('');
        $parent.find("input[type=hidden]").val('');
        $parent.find("textarea").val('');
        $parent.find("select").find("option").eq(0).prop("selected", true);
    });

    //弹出添加评分题并加数量限制
    $(".swt_but_self").click(function(){
        var $itemDiv = $("#list").find('.quItem ');
        var length = $itemDiv.length;
        if(length > 4){
            layer.msg("最多添加4道评分题！", {icon: 5});
            return;
        }
        var swt_but_name=$(this).data("sum");
        if($(".addTestQu").length>0){
            $(".addTestQu").each(function(){
                if($(this).data("sum")==swt_but_name){
                    var thmlH= $(window).height();
                    var layerH = $(this).height();
                    $(this).css("top",(thmlH-layerH)/2+"px");
                    $(this).fadeIn();
                    $(".codeLayerbj").fadeIn();
                }
            })
        }
        initSelect(-1);
    });

    // 编辑
    $("#list").on("click", '.swt_but', function(){
        var swt_but_name=$(this).data("sum");
        if($(".addTestQu").length>0){
            $(".addTestQu").each(function(){
                if($(this).data("sum")==swt_but_name){
                    var thmlH= $(window).height();
                    var layerH = $(this).height();
                    $(this).css("top",(thmlH-layerH)/2+"px");
                    $(this).fadeIn();
                    $(".codeLayerbj").fadeIn();
                    // $(".codeLayer,.codeLayerbj").fadeIn();
                }
            })
        }
        var name = $(this).parent().siblings(".dm").find('.question').val();
        $('#qiTie').val(name);
        $("#isEdit").val("edit");
        var ratio = $(this).parent().siblings(".dm").find(".ratio").val();
        initSelect(ratio);

        // $("#div-index").val($(this).parent().siblings(".dm").find('.order').val())
        $("#div-index").val($(this).parent().parent().index())
    });

    /**
     *  新增评分题目
     */
    $(".addDefine").on("click",function () {
        var question = $("#qiTie").val();
        if(isEmpty(question)){
            layer.msg("请输入评分题目！", {icon: 5});
            return;
        }
        if(getByteLen(question)>72){
            layer.msg("评分题目字数大于36！", {icon: 5});
            return;
        }
        var ratio = $("#mySelect").val();
        if (-1 == ratio) {
            layer.msg("请选择评分占比！", {icon: 5});
            return;
        }
        var isEdit = $("#isEdit").val();
        if(isEdit == 'edit'){
            var order = $("#div-index").val();
            $("#list").find(".quItem").eq(order).find(".question").val(question);
            $("#list").find(".quItem").eq(order).find(".ratio").val(ratio);
            $("#list").find(".quItem").eq(order).find(".span-ratio").text('（'+ratio+'%）');
        }else{
            var source = $("#div_template").html();
            var template = Handlebars.compile(source);
            var data = {question:question,ratio: ratio};
            var html = template(data);
            $("#list").append(html);
        }

        $(".but_none").trigger("click");
        resetOrder();
    })

    /**
     *  删除评分题目
     */
    $(".qubaBox").on("click",".deleteLink02", function () {
        var that = this;
        layer.confirm("是否确认删除",function (index) {
            $(that).parents(".quItem").remove();
            layer.close(index);
            resetOrder();
        })
    })

    /**
     *  取消
     */
    $("#cancelTemp").on("click", function () {
        window.location = "/backend/resource/sitemTemp/templet";
    })

    /**
     *  提交
     */
    $("#saveTemp").on("click",function () {
        var temp = {};
        var temp_name = $("#tempName").val();
        var sitemTempId = $("#sitemTempId").val();
        var sitemTempType = $("#sitemTempType").val();
        if(isEmpty(temp_name)){
            layer.msg("请设置评分模板名称！", {icon: 5});
            $("#tempName").focus();
            return;
        }
        temp.name = temp_name;
        temp.sitemTempId = sitemTempId;
        temp.sitemTempType = sitemTempType;
        var questions = [];
        var $qubaBox = $(".qubaBox").find(".quItem").not(":first");
        var length = $qubaBox.length;
        if(length == 0){
            layer.msg("请添加评分题目！", {icon: 5});
            $(".addQuItem").focus();
            return;
        }

        // 计算总占比
        var totalRatio=0;
        $qubaBox.each(function(index,e){
            var item = {};
            var sitemId = $(e).find("input.sitemId").val();
            var question = $(e).find("input.question").val();
            var ratio = $(e).find("input.ratio").val();
            item.sitemId = isEmpty(sitemId) ? '' : sitemId;
            item.question = question;
            item.ratio = ratio;
            totalRatio += parseInt(item.ratio);
            questions.push(item);
        })

        if (totalRatio != 100) {
            layer.msg("占比总和请设置为100%！",{icon:5})
            return;
        }

        if(checkEachQuestion(questions)){
            layer.msg("评分题目有相同,请检查!",{icon:5})
            return;
        }
        temp.questions = questions;

        $.ajax({
            type:'POST',
            dataType:'json',
            url:'/backend/resource/sitemTemp/editSitemTemp',
            data: {
                data: JSON.stringify(temp)
            },
            async:false,
            success:function(data){
                console.log(data);
                if(data.code == '00'){
                    layer.msg('保存成功!', {icon: 1});
                    setTimeout("window.location = '/backend/resource/sitemTemp/templet'", 500);
                }else {
                    layer.msg(data.message);
                }
            },
            error:function(){
                layer.msg("修改失败！", {icon: 5});
            }
        })
    })

    //初始化评分占比选项
    function initSelect(vr){
        $("#mySelect").empty();
        var item = new Option("请选择占比",-1);
        item.selected =true;
        $("#mySelect").append(item);
        var index = 0;
        for (var i = 0; i <= 20; i++) {
            var item = new Option(index + "%",index);
            if (index == vr) {
                item.selected = true;
            }
            $("#mySelect").append(item);
            index +=5;
        }
    }

    //将后台传入的排序进行转换
    function tf_num(num) {
        if(num == 1){
            return "一";
        }else if(num == 2){
            return "二";
        }else if(num == 3){
            return "三";
        }else if(num == 4){
            return "四";
        }else if(num == 5){
            return "五";
        }else if(num == 6){
            return "六";
        }else if(num == 7){
            return "七";
        }else if(num == 8){
            return "八";
        }
    }

    //判断评分题目不相同
    function checkEachQuestion(questions) {
        for(var i = 0; i < questions.length; i++) {
            var item1 = questions[i];
            for(var j = i+1; j < questions.length; j++){
                var item2 = questions[j];
                if(item1.question == item2.question && item1.ratio == item2.ratio){
                    return true;
                }
            }
        }
        return false;
    }

    //判断输入的题目字数
    function getByteLen(val) {
        var len = 0;
        for (var i = 0; i < val.length; i++) {
            var a = val.charAt(i);
            if (a.match(/[^\x00-\xff]/ig) != null)
            {
                len += 2;
            }
            else
            {
                len += 1;
            }
        }
        return len;
    }

    function resetOrder() {
        var $itemDiv = $("#list").find('.quItem ');
        var length = $itemDiv.length;
        $itemDiv.each(function (index, e) {
            $(e).find(".dt").find('span.selectTxt').text(tf_num(index))
        })
    }
})

