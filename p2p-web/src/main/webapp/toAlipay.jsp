<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/8/14
  Time: 16:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>疯狂加载中...</title>
</head>
<body>
<form action="${p2p_pay_alipay_url}" method="post">
    <input type="hidden" name="out_trade_no" value="${rechargeNo}"/>
    <input type="hidden" name="total_amount" value="${rechargeMoney}"/>
    <input type="hidden" name="subject" value="${subject}"/>
    <input type="hidden" name="body" value="${body}"/>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
