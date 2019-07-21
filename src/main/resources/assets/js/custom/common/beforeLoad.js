$.ajax({
    type: 'GET',
    url: "/backend/isLogin",
    data: {},
    dataType: "json",
    async: false,
    cache: false,
    success: function(data){
        if(data.code != '00'){
            window.location.replace("/backend/login");
            event.returnValue=false;
        }
    },
    error: function(){
        window.location.replace("/backend/login");
        event.returnValue=false;
    }
});