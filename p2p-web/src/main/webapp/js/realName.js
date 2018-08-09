//验证真实名字格式
function userRealName(flag) {
	var realName = $.trim($("#realName").val());
	var rtn = false;
	if (null==realName || realName=="") {
		showError("realName", "真实姓名不能为空");
		return false;
	} else if (!/[^\x00-\x80]/.test(realName)) {	
		showError("realName", "请输入真实的中文姓名");
		return false;
	} else {
		showSuccess('realName');
		rtn = true;
	}
	if(!rtn){
		return false;
	}
	return true;
}

//身份证号码验证
function idCardCheck() {
	var rtn = false;
	
	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());
	
	if (idCard=="") {
		showError('idCard','请输入身份证号码');
		return false;
	}
	//身份证号码为15位或18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
	var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
	if (!(reg.test(idCard))) {
		showError('idCard','请输入正确的身份证号码');
		return false;
	} else if (idCard.length < 15 || idCard.length > 18 || idCard.length == 17) {
		showError('idCard','身份证号码应为15或18位');
		return false;
	} else {
		
		$.ajax({
			url:"loan/checkIdCard",
			type:"POST",
			dataType:"json",
			async:false,
			data:"idCard="+idCard,
			success:function(jsonMap) {
				if(jsonMap.errorMessage == "ok") {
					showSuccess("idCard");
					rtn = true;
				} else {
					showError("idCard",jsonMap.errorMessage);
					rtn = false;
				}
			},
			error:function() {
				showError("idCard","网络错误");
				rtn = false;
			}
		});
		
	}
	
	if (replayIdCard != null && replayIdCard != null && idCard == replayIdCard) {
		showSuccess('replayIdCard');
	}
	
	if(!rtn){
		return false;
	}
	return true;
}

//两次密码是否相等验证
function idCardEequ() {
	var idCard=$.trim($("#idCard").val());//身份证
	var replayIdCard=$.trim($("#replayIdCard").val());//确认身份证
	
	if (!idCardCheck()) {
		hideError('replayIdCard');
		return false;
	}
	if (idCard == "") {
		showError('idCard','请输入身份证号码！');
		return false;
	} else if(replayIdCard == "") {
		showError('replayIdCard','请再次输入身份证号码');
		return false;
	} else if(idCard!=replayIdCard) {
		showError('replayIdCard','两次输入身份证号码不一致');
		return false;
	} else {
		showSuccess('replayIdCard');
		return true;
	}
	return true;
}

//验证码验证
function checkCaptcha() {
	var rtn = false;
	var captcha = $.trim($("#captcha").val());
	if (captcha == "") {
		showError('captcha','请输入图形验证码');
		return false;
	} else {
		$.ajax({
			type:"POST",
			url:"loan/checkCaptcha",
			async: false,
			data:"captcha="+captcha,
			success: function(retMap) {
			    if (retMap.errorCode) {
			    	showSuccess('captcha');
			    	rtn = true;
			    } else {
			    	showError('captcha', retMap.errorMessage);
			    	rtn = false;
			    }
			},
		    error:function() {
				showError('captcha','网络错误');
				rtn = false;
			}
		});
	}
	if (!rtn) {
		return false;
	}
	return true;
} 

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}

//成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//实名认证提交
function realName () {
	
	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());//确认身份证号
	var realName = $.trim($("#realName").val());
	var captcha = $.trim($("#captcha").val());
	
	if(userRealName(0) && idCardEequ() && checkCaptcha() && idCardCheck()) {
		$.ajax({
			type:"POST",
			url:"loan/checkRealName",
			dataType: "json",
			async: false,
			data:"realName="+realName+"&idCard="+idCard+"&replayIdCard="+replayIdCard+"&captcha="+captcha,
			success: function(retMap) {
				if (retMap.errorMessage == "ok") {
					window.location.href = "loan/showMyCenter";
				} else {
					showError('captcha', retMap.errorMessage);
				}
			},
		    error:function() {
				 showError('captcha','网络错误');
				 rtn = false;
			}
		});
	}
}
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}