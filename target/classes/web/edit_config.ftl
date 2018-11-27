<div class="layui-container">

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 50px;">
        <legend>微信群监控配置！</legend>
    </fieldset>


    <form class="layui-form" id="form-box">
        <input type="hidden" name="id" <#if config??>value="${config.id!'0'}"<#else>value="0"</#if> id="id">
        <div class="layui-form-item">
            <label class="layui-form-label">姓名</label>
            <div class="layui-input-inline">
                <input type="text" name="onCall1Name" id="onCall1Name" lay-verify="required" autocomplete="off" placeholder="请输入值班人姓名" class="layui-input" <#if config??>value="${config.onCall1Name!''}"</#if>>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">手机号</label>
            <div class="layui-input-inline">
                <input type="text" name="onCall1Phone" id="onCall1Phone" lay-verify="required|phone" placeholder="请输入值班人手机号" autocomplete="off" class="layui-input" <#if config??>value="${config.onCall1Phone!''}"</#if>>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">监听微信群</label>
        <#--<div class="layui-input-inline">-->
        <#--<select name="chatroomNickName" lay-filter="chatroomNickName" id="chatroomNickName">-->
        <#--<#list chatRooms as room>-->
        <#--<option value="${room.nickName}" <#if config?? && room.nickName == config.chatroomNickName>selected</#if>>${room.nickName}</option>-->
        <#--</#list>-->
        <#--</select>-->
        <#--</div>-->
            <div class="layui-input-inline">
                <input type="text" name="chatroomNickName" id="chatroomNickName" lay-verify="required" autocomplete="off" placeholder="请输入监听微信群名称" class="layui-input" <#if config??>value="${config.chatroomNickName!''}"</#if>>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">停止电话关键词</label>
            <div class="layui-input-inline">
                <input type="text" name="stopCallKeyWords" id="stopCallKeyWords" lay-verify="required" placeholder="请输入群关键词" autocomplete="off" class="layui-input" <#if config??>value="${config.stopCallKeyWords!''}"</#if>>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">开始日期</label>
                <div class="layui-input-inline">
                    <input type="text" name="onDutyStartDate" id="onDutyStartDate" lay-verify="required|date" placeholder="yyyy-MM-dd" autocomplete="off" class="layui-input" <#if config??>value="${config.onDutyStartDate?string('yyyy-MM-dd')}"</#if>>
                </div>
            </div>
        </div>

        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">结束日期</label>
                <div class="layui-input-inline">
                    <input type="text" name="onDutyEndDate" id="onDutyEndDate" lay-verify="required|date" placeholder="yyyy-MM-dd" autocomplete="off" class="layui-input" <#if config??>value="${config.onDutyEndDate?string('yyyy-MM-dd')}"</#if>>
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="submitSave" >立即提交</button>
            </div>
        </div>
    </form>

</div>
<script>
    layui.use(['form', 'table', 'laydate'], function(){
        var form = layui.form
                ,layer = layui.layer
                ,laydate = layui.laydate
                ,table = layui.table;

        //日期
        laydate.render({
            elem: '#onDutyStartDate'
        });
        laydate.render({
            elem: '#onDutyEndDate'
        });


        //监听提交
        form.on('submit(submitSave)', function(data){
            $.ajax({
                type: "POST",
                url: "saveConfig",
                data:{
                    "id":$("#id").val(),
                    "onCall1Name":$("#onCall1Name").val(),
                    "onCall1Phone":$("#onCall1Phone").val(),
                    "chatroomNickName":$("#chatroomNickName").val(),
                    "stopCallKeyWords":$("#stopCallKeyWords").val(),
                    "onDutyStartDate":new Date($("#onDutyStartDate").val()+" 00:00:00"),
                    "onDutyEndDate":new Date($("#onDutyEndDate").val()+" 23:59:59")
                },
                success: function (msg) {
                    layer.msg(msg);
                    setTimeout(function(){window.location.reload();}, 500);
                },
                error: function (error) {
                    layer.msg("server error");
                }
            });
            return false;
        });


        // //监听工具条
        // table.on('tool(demo)', function(obj){
        //     var data = obj.data;
        //     if(obj.event === 'del'){
        //         layer.confirm('真的删除行么', function(index){
        //             obj.del();
        //             layer.close(index);
        //         });
        //     } else if(obj.event === 'edit'){
        //         layer.alert('编辑行：<br>'+ JSON.stringify(data))
        //     }
        // });

    });


</script>