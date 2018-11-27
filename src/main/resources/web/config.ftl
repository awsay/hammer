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


<div style="width: 80%; position: relative; left:10%;">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>监控排班列表</legend>
    </fieldset>
    <div class="layui-form layui-form-pane">
        <table class="layui-table">
            <colgroup>
                <col width="150">
                <col width="150">
                <col width="200">
                <col>
            </colgroup>
            <thead>
            <tr>
                <th>id</th>
                <th>姓名</th>
                <th>手机号</th>
                <th>监听微信群</th>
                <th>微信群消息keywords</th>
                <th>开始日期</th>
                <th>开始日期</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
        <#list configList as config>
        <tr>
            <td>${config.id}</td>
            <td>${config.onCall1Name}</td>
            <td>${config.onCall1Phone}</td>
            <td>${config.chatroomNickName}</td>
            <td>${config.stopCallKeyWords}</td>
            <td>${config.onDutyStartDate?string('yyyy-MM-dd')}</td>
            <td>${config.onDutyEndDate?string('yyyy-MM-dd')}</td>
            <td>
                <a class="layui-btn layui-btn-xs" href="javascript:edit(${config.id});">编辑</a>
                <a class="layui-btn layui-btn-danger layui-btn-xs" href="javascript:del(${config.id});">删除</a>
            </td>
        </tr>
        </#list>
            </tbody>
        </table>


        <div class="layui-form-item">
            <button class="layui-btn" lay-submit="" onclick="addListen();">添加监控</button>
        </div>

    </div>
</div>


<script type="text/javascript" src="/lib/layui/layui.js"></script>
<script>
    function addListen() {
        $.ajax({
            type: "GET",
            url: "editConfig",
            success: function (msg) {
                layer.open({
                    type: 1,
                    shade: false,
                    title: false, //不显示标题
                    content:msg
                });
                return false;
            },
            error: function (error) {
                layer.msg("server error");
            }
        });

    }

    function edit(id) {
        $.ajax({
            type: "GET",
            url: "editConfig",
            data:{
                "id":id
            },
            success: function (msg) {
                layer.open({
                    type: 1,
                    shade: false,
                    title: false, //不显示标题
                    content:msg
                });
            },
            error: function (error) {
                layer.msg("server error");
            }
        });
    }

    function del(id) {
        layer.confirm('真的删除该记录么？', function(index){
            $.ajax({
                type: "GET",
                url: "delConfig",
                data:{
                    "id":id
                },
                success: function (msg) {
                    layer.msg("delete success!");
                    obj.del();
                    layer.close(index);
                },
                error: function (error) {
                    layer.msg("server error");
                }
            });
        });
    }

</script>

</body>


</html>
