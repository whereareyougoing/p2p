package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 宋艾衡 on 2018/8/10 上午10:35
 */
@Controller
public class UserController {



	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private UserService userService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountService financeAccountService;

	@PostMapping("/loan/checkPhone")
	@ResponseBody
	public Object checkPhone(HttpServletRequest request,@RequestParam(value = "phone",required = true) String phone){

		Map<String,Object> retMap = Maps.newHashMap ();

		User user = userService.queryUserPhone(phone);

		if (user != null){
			retMap.put ( Constants.ERROR_MESSAGE,"手机号已经被注册" );
			return retMap;
		}

		retMap.put ( Constants.ERROR_MESSAGE,Constants.OK );
		return retMap;

	}


	@PostMapping("/loan/checkCaptcha")
	@ResponseBody
	public Map<String,Object> checkCaptcha(HttpServletRequest request,
										   @RequestParam(value = "captcha",required = true)String captcha){
		Map<String,Object> retMap = Maps.newHashMap ();
		String sessionCaptcha = (String) request.getSession ().getAttribute ( Constants.CAPTCHA );

		if (!StringUtils.equalsIgnoreCase ( captcha,sessionCaptcha )){
			retMap.put ( Constants.ERROR_MESSAGE,"验证码错误" );
			return retMap;
		}

		retMap.put ( Constants.ERROR_MESSAGE,Constants.OK );
		return retMap;
	}

	@PostMapping("/loan/register")
	@ResponseBody
	public Map<String,Object> register(HttpServletRequest request,
									   @RequestParam(value = "phone",required = true)String phone,
									   @RequestParam(value = "loginPassword",required = true)String loginPassword,
									   @RequestParam(value = "replayLoginPassword",required = true)String replayLoginPassword){

		Map<String,Object> retMap = Maps.newHashMap ();
		if (Pattern.matches ( "^1[1-9]\\d{9}$",phone )){
			retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
			return retMap;
		}

		if (!StringUtils.equals(loginPassword,replayLoginPassword)) {
			retMap.put(Constants.ERROR_MESSAGE,"两次输入密码不一致");
			return retMap;
		}


		ResultObject ro = userService.register(phone,loginPassword);

		if (StringUtils.equals ( ro.getErrorCode (),Constants.SUCCESS )){
			request.getSession ().setAttribute ( Constants.SESSION_USER,userService.queryUserPhone ( phone ) );
			retMap.put ( Constants.ERROR_MESSAGE,Constants.OK );
		}else {
			retMap.put ( Constants.ERROR_MESSAGE,"注册失败" );
			return retMap;
		}

		return retMap;

	}

	@PostMapping("/loan/logout")
	@ResponseBody
	public String logout(HttpServletRequest request){
		request.getSession ().invalidate ();
		return "redirect:/index";
	}




}
