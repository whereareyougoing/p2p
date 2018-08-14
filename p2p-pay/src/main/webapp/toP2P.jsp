<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/8/14
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>疯狂加载中...</title>
</head>
<body>
<form action="${pay_p2p_return_url}" method="post">
    <input type="hidden" name="signVerified" value="${signVerified}"/>
    <c:choose>
        <c:when test="${not empty paramMap}">
            <c:forEach items="${paramMap}" var="paramMaps">
                <input type="hidden" name="${paramMaps.key}" value="${paramMaps.value}"/>
            </c:forEach>
        </c:when>
    </c:choose>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
