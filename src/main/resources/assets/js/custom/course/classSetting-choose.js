$(function(){
    var courseId = $("#courseId").val();

    //点击自动
    $("#automatic").click(function(){
        location.href = "/backend/course/classSetting?sysId="+courseId+"&classState=0";
    });

    //点击手动
    $("#manual").click(function(){
        location.href = "/backend/course/classSetting?sysId="+courseId+"&classState=1";
    });
});