var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});
//输入域失去焦点，将红色提示信息清除
$(document).ready(function() {
	$("#phone").blur(function() {  
		$("#showId").html("");
	});
	$("#loginPassword").blur(function() {  
		$("#showId").html("");
	});
	loadStat();
});

//用户登录
function Login() {
	var phone = $("#phone").val();
	var loginPassword = $("#loginPassword").val();
	var captcha = $("#captcha").val();
	if (phone == "" || phone == "请输入11位手机号码") {
		$("#showId").html("请输入11位手机号码");
		return false;
	}
	//判断手机号码合法性正则表达式
	if (!/^1[1-9]\d{9}$/.test(phone)) {
		$("#showId").html("请输入正确的手机号");
		return false;
	}
	if (loginPassword == "" || loginPassword == "请输入登录密码") {
		$("#showId").html("请输入登录密码");
		return false;
	}
	if (loginPassword.length<6 || loginPassword.length>16) {
		$("#showId").html("登录密码为6到16位");
		return false;
	}
	if (captcha=="" && $("#showCaptcha").css('display') == "block"){
		$("#showId").html("请输入图形验证码");
		return false;
	}
	$.ajax({
	   type:"POST",
	   url:"loan/login",
	   dataType: "json",
	   async: false,
	   data:"phone="+phone+"&loginPassword="+$.md5(loginPassword)+"&captcha="+captcha,
	   success: function(errorMessage) {
		   $.each(errorMessage, function(key, value) {
	            if (value=="ok") {
	            	if ("" != referrer) {
			    		window.location.href = referrer;
			    	} else {
				    	window.location.href = "loan/showMyCenter";
			    	}
	            } else {//验证或登录不成功
	            	$("#showId").html(value);
	            	return false;
	            }
	       });
	   	}
	});
}
//用户及交易统计
function loadStat () {
	$.ajax({
	   type:"POST",
	   url:"loan/stat",
	   dataType: "json",
	   async: false,
	   success: function(jsonObject) {
		   $("#user").html(jsonObject.allUserCount);
		   $("#gold").html(jsonObject.allBidMoney);
	   	}
	});
}