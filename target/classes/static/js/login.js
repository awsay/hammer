$(document).ready(function () {

    while (true){
        $.ajax({
            type: "POST",
            url: "loginStatus",
            async: false,
            data: {
                "version" : $("#version").val()
            },
            success: function (msg) {
                if(msg == "REFRESH"){
                    window.location.reload();
                    return false;
                }else if(msg == "REFRESH"){
                    $("#wechat-code").attr("src","/images/login-success.jpg")
                    $("#to-config-page").show();
                    setTimeout(function(){window.location.href="/config";}, 1500);
                }
            },
            error: function (error) {
                layer.msg("server error");
            }
        })
    }
});