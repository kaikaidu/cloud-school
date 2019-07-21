// JavaScript Document
var layer, laypage, layupload;
$(document).ready(function () {
    layer = layui.layer;
    laypage = layui.laypage;
    layupload = layui.upload;

    $.ajaxSetup({
        cache: false,
        beforeSend:function(){
            layer.load(2);
        },
        complete: function() {
            layer.closeAll('loading');
        }
    });

	/*左侧菜单背景块高度*/
	var bodyH= $(document).height();
	var hdH= $(".head").height();
	$(".menuFl").height(bodyH-hdH-50);
	
	/*01课程管理》学员状态》checkbox 全选*/
	 $(function() {
        $("#selectAll").click(function() {
            $(":checkbox[name='ids']").prop("checked", this.checked); // this指代的你当前选择的这个元素的JS对象
        });
    });
	 // 浮层管理str
//	var thmlH= $(window).height();
//	var layerH = $(".addTestQu").height();
//	$(".addTestQu").css("top",(thmlH-layerH)/2+"px");
	// 点击按钮出现弹层str
	$(".swt_but").click(function(){
		var swt_but_name=$(this).data("sum");
		if($(".addTestQu").length>0){
			$(".addTestQu").each(function(){
				if($(this).data("sum")==swt_but_name){
					$(this).fadeIn();
					$(".codeLayerbj").fadeIn();
					/*//滚动条
					var hscroll = $(".scroll40").height();
					if(hscroll>400){
						$(".scroll40").css({"overflow-y":"scroll","height":"400px"});
					}else{
						$(".scroll40").css({"overflow":"visible","height":"auto"});
					}
					//滚动条
					var hscrol2 = $(".scroll30").height();
					console.log(hscrol2)
					if(hscrol2>267){
						$(".scroll30").css({"overflow-y":"scroll","height":"267px"});
					}else{
						$(".scroll30").css({"overflow":"visible","height":"auto"});
					}*/
					// $(".codeLayer,.codeLayerbj").fadeIn();
					var thmlH= $(window).height();
					var layerH = $(this).height();
					$(this).css("top",(thmlH-layerH)/2+"px");
					
				}
				
			})
		}
		
	});
	// 点击弹层的关闭按钮时str
	$(".addTestQu .close").click(function(){
		$(this).parents(".addTestQu").fadeOut();
		$(".codeLayerbj").fadeOut();
	});
	// 点击弹层的取消按钮时
	$(".addTestQu .but_none").click(function(){
		$(this).parents(".addTestQu").fadeOut();
		$(".codeLayerbj").fadeOut();
	});
	// 评分题库-已新增评分模板弹层关闭按钮 str
	$(".addTestQu .closeBt").click(function(){
		$(this).parent(".addTestQu").fadeOut();
		$(".codeLayerbj").fadeOut();
	});
	
	/**02*/
	var thmlH= $(window).height();
	 var layerH = $(".codeLayer2").height();
	$(".codeLayer2").css("top",(thmlH-layerH)/2+"px");
	$(".swt_but2").click(function(){
		var swt_but_name=$(this).data("sum");
		if($(".codeLayer2").length>0){
			$(".codeLayer2").each(function(){
				if($(this).data("sum")==swt_but_name){
					$(this).fadeIn();
					$(".codeLayerbj2").fadeIn();
	/*//滚动条
	var hscroll = $(".scroll40").height();
	if(hscroll>400){
		$(".scroll40").css({"overflow-y":"scroll","height":"400px"});
	}else{
		$(".scroll40").css({"overflow":"visible","height":"auto"});
	}*/
					// $(".codeLayer,.codeLayerbj").fadeIn();
					var thmlH= $(window).height();
					var layerH = $(this).height();
					$(this).css("top",(thmlH-layerH)/2+"px");
					
	/*//滚动条
	var hscroll = $(".scroll40").height();
	if(hscroll<400){
		$(".scroll40").css({"overflow-y":"scroll","height":"auto"});
	}else{
		$(".scroll40").css({"overflow":"visible","height":"400px"});
	}*/
				}
				
			})
		}
		
	});
	// 点击弹层的关闭按钮时str
	$(".codeLayer2 .close").click(function(){
		$(this).parent().parent(".codeLayer2").fadeOut();
		$(".codeLayerbj2").fadeOut();
	});
	// 点击弹层的取消按钮时
	$(".codeLayer2 .but_none").click(function(){
		$(this).parent().parent().parent(".codeLayer2").fadeOut();
		$(".codeLayerbj2").fadeOut();
	});
	// 评分题库-已新增评分模板弹层关闭按钮 str
	$(".codeLayer2 .closeBt").click(function(){
		$(this).parent(".codeLayer2").fadeOut();
		$(".codeLayerbj2").fadeOut();
	});
	
	/*登录*/
	$(".loginWp").height(bodyH);
	var logH = $(".loginCon").height();
	$(".loginCon").css("top",(bodyH-logH)/2+"px");	
	$(window).resize(function(){		
		$(".loginWp").height(bodyH);
		var logH = $(".loginCon").height();
		$(".loginCon").css("top",(bodyH-logH)/2+"px");
	});
	

	// 预览切换 str 
	$(".w_yl_com_bt li").each(function(index){

		$(this).click(function(){
			$(".w_yl_com_bt li").removeClass("cur");
			$(this).addClass("cur");
			var w_a1=index;
			
			$(".w_yl_com").hide();
			$(".w_yl_com").eq(w_a1).fadeIn(1);
		})
	})
	
	
	/*radio checkbox style*/
	;(function($){
	$.extend({
	inputStyle:function(){
	function check(el,cl){
	$(el).each(function(){
	$(this).parent('i').removeClass(cl);
	var checked = $(this).prop('checked');
	if(checked){
	$(this).parent('i').addClass(cl);
	}
	})
	}
	$('input[type="radio"]').on('click',function(){
	check('input[type="radio"]','radio_bg_check');
	})
	$('input[type="checkbox"]').on('click',function(){
	check('input[type="checkbox"]','checkbox_bg_check');
	})
	}
	})
	})(jQuery)
	//调用
	$(function(){
	$.inputStyle();
	})
});
