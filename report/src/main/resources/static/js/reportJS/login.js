
$(function() {
    console.log("进入JS")
    $('#form1').submit(function() { //当提交表单时，会发生 submit 事件。
        //此处可做表单验证
        if ($("#username").val() == "") {
            alert("用户名不能为空");
            return false;
        }
        if ($("#password").val() == "") {
            alert("密码错误");
            return false;
        }
        // 如果页面有表单，那么可以利用jquery的serialize()方法获取表单的全部数据
        var postData = $("#form1").serialize(); //序列化表单，后台可正常通过post方法获取数据
        console.log(postData);
        $.ajax({
            type: "POST",
            url: "/login",
            dataType:'json',
            data: postData,
            beforeSend: function() {
                $("#btn_login").attr("disabled", true);//提交表单前的处理，防止用户多次点击【登陆】，重复提交表单
                $("#btn_login").val("正在登陆...");
            },
            success: function(data) {
                alert(111);
                console.log(data);
                if (data.code == '0') {
                    console.log("进入成功")
                    window.location.href = '/loginSuccess';
                } 
                else {
                    $("#btn_login").attr("disabled", false);
                    $("#btn_login").val("登陆");
                    alert("用户或密码错误！");
                }
            }
        });
    });
});