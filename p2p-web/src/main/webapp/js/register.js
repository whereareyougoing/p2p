//验证手机号
function checkPhone() {
//    获取手机号
    var phone = $.trim($("#phone").val());
    var flag = true;
    
    if ('' == phone){
        showError("phone","请输入手机号");
        return false; // 返回false的字符
    } else if (!/^1[1-9]\d{9}$/.test(phone)){
        showError("phone","手机号格式不正确");
        return false;
    }else {
    //    ajax 不能直接返回true或者false
        $.ajax({
            url:"loan/checkPhone",
            type: "post",
            data: {
                "phone": phone
            },
            async: false,
            success: function (json) {
                //后台返回json类型的数据，key为message，value为success或者fail
                if (json.errorMessage = "ok"){
                    showSuccess("phone",json.errorMessage);
                    flag = true;
                }else{
                    showError("phone",json.errorMessage);
                    flag = false;
                }
            },
            error:function () {
                showError("phone","系统繁忙，请稍后再试");
                flag = false;
            }
        });
    }

    if (!flag){
        return false;
    }

    return true;
}

//验证注册密码
function checkLoginPassword() {
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    if('' == loginPassword) {
        showError("loginpassword","密码不能为空");
        return false;
    }else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)){

    }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
        showError("loginPassword","密码应为数字和字母组成");
        return false;
    }else if (loginPassword.length < 6 || loginPassword > 16){
        showError("loginPassword","密码长度应大于6位小于16位");
        return false;
    } else {
        showSuccess("loginPassword");
    }

    if (replayLoginPassword != loginPassword) {
        showError("replayLoginPassword","两次输入密码不一致");
    }

    return true;

}

//验证确认注册密码
function checkLoginPasswordEqu() {
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    if ("" == loginPassword) {
        showError("loginPassword","请输入登录密码");
        return false;
    } else if("" == replayLoginPassword) {
        showError("replayLoginPassword","请输入确认登录密码");
        return false;
    } else if(replayLoginPassword != loginPassword) {
        showError("replayLoginPassword","两次输入密码不一致");
        return false;
    } else {
        showSuccess("replayLoginPassword");
    }

    return true

}

//验证图形验证码
function checkCaptcha() {

//    获取图形验证码
    var captcha = $.trim($("#captcha").val());
    var flag = true;

    if ('' == captcha) {
        showError("", "验证码为空");
        return false;
    } else {
    //    提交后台验证
        $.ajax({
            url:"loan/checkCaptcha"
            type:"post",
            data:{
                "captcha":captcha
            },
            async:false,
            success:function (jsonObject) {
                if (jsonObject.errorMessage == "ok"){
                    showSuccess("captcha");
                    flag = true;
                }else {
                    showError("captcha",jsonObject.errorMessage);
                    flag = false;
                }
            },
            error:function () {
                showError("phone","系统繁忙，请稍后再试");
                flag = false;
            }
        });

        if (!flag){
            return false;
        }
        return true;
    }

}


//注册
function register() {
//    获取用户注册的表单信息
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());
    var resplayLoginPassword = $.trim($("#replayLoginPassword").val());

    if (checkPhone() && checkLoginPassword() && checkLoginPasswordEqu() && checkCaptcha()){
        $("#loginPassword").val($.md5(loginPassword));
        $("#resplayLoginPassword").val($.md5(resplayLoginPassword));

        $.ajax({
            url: "loan/register",
            type: "post",
            data: {
                "phone": phone,
                "loginPassword": $("#loginPassword").val(),
                "resplayLoginPassword":$("#resplayLoginPassword").val()
            },
            success:function (jsonObject) {
                if (jsonObject.errorMessage == "ok"){
                    window.location.href == "realName.jsp";
                }else {
                    showError("register",jsonObject.errorMessage);
                }
            },
            error:function () {
                showError("register","系统繁忙，请稍后再试");
            }
        });

    }
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
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
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