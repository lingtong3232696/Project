<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="zh-CN">
<head>
    <title>TestDemo</title>
    <script type="text/javascript" src="js/jquery.js"></script>
</head>
<script>
    $(function () {
        $.ajax({
            url:"user/test",
            type:"POST",
            data:{id:2},
            dataType:"json",
            success:function (datas) {
                console.log(datas);
                var json = JSON.stringify(datas);
                $("#userDemo").html(json);
            },
            error:function () {
                confirm("请求失败");
            }
        });

    });

</script>
<body>
<h2>Hello World!</h2>
<div id="userDemo"></div>
</body>
</html>
