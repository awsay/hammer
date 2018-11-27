<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">

<html xmlns="http://www.w3.org/1999/html">
<head>
    <link rel="stylesheet" href="/css/style.css" media="all" />
    <link rel="stylesheet" href="/lib/layui/css/layui.css" media="all" />
    <script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="/js/layer.js"></script>
    <title>login</title>
</head>

<body >
<!--[if lt IE 9]>
<script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
<script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->

<div class="layui-container">

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
        <legend>微信扫描二维码登录！</legend>
    </fieldset>


        <div class="layui-tab-content">
            <div class="layui-tab-item layui-show">
                <div >
                    <div id="layer-photos-godaddy1" class="text-align-center">
                        <img class="absolute-center" id="wechat-code" style="width: 400px;" src="data:img/jpg;base64,${base64Img!'未获取到登录二维码'}">
                        <p id="to-config-page" style="display: none">即将跳转到配置界面....</p>
                    </div>
                </div>
            </div>
        </div>
        <input type="hidden" id="version" value="${version}"/>
</div>


<script type="text/javascript" src="/lib/layui/layui.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        loginStatus()
    });

    function loginStatus() {
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
                }else if(msg == "LOGIN_SUCCESS"){
                    $("#wechat-code").attr("src","/images/login-success.jpg")
                    $("#to-config-page").show();
                    setTimeout(function(){window.location.href="/toConfigPage";}, 1500);
                }else{
                    setTimeout(function(){loginStatus();}, 1000);
                }
            },
            error: function (error) {
                layer.msg("server error");
            }
        })
    }
</script>


</body>


</html>
