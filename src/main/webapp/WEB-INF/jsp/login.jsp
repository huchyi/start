<%--
  Created by IntelliJ IDEA.
  User: zhangnan
  Date: 16/7/23
  Time: 下午4:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
    <title>登陆</title>
</head>
<body>
<div>
  <div>账号: <input type="text" id="username"></div>
  <div>密码: <input type="text" id="password"></div>
  <div><input type="button" value="登陆" onclick="login()"/></div>
</div>

</body>
<script type="text/javascript" src="../../static/js/jquery-1.10.2.min.js"></script>


<script type="text/javascript">

  function login(){
    if($("#username").val()==null || $("#username").val()==""){
      alert("账号不能为空");
      return false;
    }
    if($("#password").val()==null || $("#password").val()==""){
      alert("密码不能为空");
      return false;
    }
    //登陆
    $.ajax({
      type: "POST",
      url: "http://localhost:8080/user/login",
      dataType: "json",
      data:{ 'username':$("#username").val(),'password':$("#password").val()},
      success: function(msg){
        if(msg){
          window.location='http://localhost:8080/article/queryArticleList';
        }else{
          alert("账号或密码错误");
        }
      }
    });
  }



</script>
</html>
