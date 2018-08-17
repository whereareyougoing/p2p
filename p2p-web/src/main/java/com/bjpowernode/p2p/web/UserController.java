package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.comman.util.HttpClientUtils;
import com.bjpowernode.p2p.config.Config;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.loan.RechargeRecoudService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 宋艾衡 on 2018/8/10 上午10:35
 */
@Controller
public class UserController {



	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private Config config;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private UserService userService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountService financeAccountService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private LoanInfoService loanInfoService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private BidInfoService bidInfoService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private RechargeRecoudService rechargeRecoudService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private IncomeRecordService incomeRecordService;

	/**
	 * 验证手机号是否存在
	 * 网关地址：http://localhost:8080/p2p/loan/checkPhone
	 * 请求方式：只支持POST
	 * @param request
	 * @param phone 必填
	 * @return json格式的字符串｛"errorMessage":"提示的信息"｝
	 */
//    @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.POST) // 等价于 @PostMapping(value="/loan/checkPhone")
//    @PostMapping(value = "/loan/checkPhone")
//    @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.GET) // 等价于 @GetMapping(value="/loan/checkPhone")
	@GetMapping(value = "/loan/checkPhone")
	@ResponseBody
	public Object checkPhone(HttpServletRequest request,
							 @RequestParam (value = "phone",required = true) String phone) {
		Map<String,Object> retMap = new HashMap<String,Object> ();

		//根据手机号查询用户信息(手机号) -> 返回User**|boolean|int
		User user = userService.queryUserByPhone(phone);

		//判断用户是否存在
		if (null != user) {
			//该用户存在，手机号已被注册，请更换手机号码
			retMap.put(Constants.ERROR_MESSAGE,"手机号已被注册，请更换手机号码");
			return retMap;
		}

		retMap.put(Constants.ERROR_MESSAGE,Constants.OK);


		return retMap;
	}

	@PostMapping(value = "/loan/checkCaptcha")
	public @ResponseBody Map<String,Object> checkCaptcha(HttpServletRequest request,
														 @RequestParam (value = "captcha",required = true) String captcha) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		//从session中获取图形验证码
		String sessionCaptcha = (String) request.getSession().getAttribute(Constants.CAPTCHA);

		if (!StringUtils.equalsIgnoreCase(sessionCaptcha,captcha)) {
			retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
			return retMap;
		}

		retMap.put(Constants.ERROR_MESSAGE,Constants.OK);


		return retMap;
	}


	@RequestMapping(value = "/loan/register")
	public @ResponseBody Map<String,Object> register(HttpServletRequest request,
													 @RequestParam (value = "phone",required = true) String phone,
													 @RequestParam (value = "loginPassword",required = true) String loginPassword,
													 @RequestParam (value = "replayLoginPassword",required = true) String replayLoginPassword) {
		Map<String,Object> retMap = new HashMap<String,Object>();

		//------参数验证-------------
		if (!Pattern.matches("^1[1-9]\\d{9}$",phone)) {
			retMap.put(Constants.ERROR_MESSAGE,"请输入正确的手机号码");
			return retMap;
		}

		if (!StringUtils.equals(loginPassword,replayLoginPassword)) {
			retMap.put(Constants.ERROR_MESSAGE,"两次输入密码不一致");
			return retMap;
		}


		//用户注册【新增用户，开立帐户】(手机号，登录密码) -> 返回int|boolean|结果对象ResultObject
		ResultObject resultObject = userService.register(phone,loginPassword);

		//判断用户是否注册成功
		if (StringUtils.equals(resultObject.getErrorCode(),Constants.SUCCESS)) {
			//将用户的信息存放到session中
			request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(phone));

			retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

		} else {
			retMap.put(Constants.ERROR_MESSAGE,"注册失败，请稍后重试...");
			return retMap;
		}

		return retMap;
	}

	@RequestMapping(value = "/loan/myFinanceAccount")
	public @ResponseBody Object myFinanceAccount(HttpServletRequest request) {

		//从session中获取用户信息
		User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

		//根据用户标识获取当前帐户资金信息
		FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

		return financeAccount;
	}

	@RequestMapping(value = "/loan/logout")
	public String logout(HttpServletRequest request) {

		//让session失效或者清除session
		request.getSession().invalidate();
//        request.getSession().removeAttribute(Constants.SESSION_USER);

		return "redirect:/index";
	}


	@RequestMapping(value = "/loan/verifyRealName")
	public @ResponseBody Object verifyRealName(HttpServletRequest request,
											   @RequestParam (value = "realName",required = true) String realName,
											   @RequestParam (value = "idCard",required = true) String idCard,
											   @RequestParam (value = "replayIdCard",required = true) String replayIdCard) {
		Map<String,Object> retMap = new HashMap<String,Object>();

		//验证参数
		if (!Pattern.matches("^[\\u4e00-\\u9fa5]{0,}$",realName)) {
			retMap.put(Constants.ERROR_MESSAGE,"真实姓名只支持中文");
			return retMap;
		}

		if (!Pattern.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",idCard)) {
			retMap.put(Constants.ERROR_MESSAGE,"请输入正确的身份证号码");
			return retMap;
		}

		if(!StringUtils.equals(idCard,replayIdCard)) {
			retMap.put(Constants.ERROR_MESSAGE,"两次身份证号码不一致");
			return retMap;
		}

		//进行实名认证,调用互联网的实名认证接口
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("appkey",config.getRealNameAppkey());//接口请求的key
		paramMap.put("cardNo",idCard);//身份证号码
		paramMap.put("realName",realName);//真实姓名

		//调用互联网实名认证接口-> 返回json格式的字符串
		String resultJson = HttpClientUtils.doPost(config.getRealNameUrl(), paramMap);
//        String resultJson = "{\"code\":\"10000\",\"charge\":false,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"乐天磊\",\"idcard\":\"350721197702134399\",\"isok\":true}}}";


		//解析json格式的字符串，使用fastJson
		//1.将json格式的字符串转换为JSON对象
		JSONObject jsonObject = JSONObject.parseObject(resultJson);

		//2.获取指定的key所对应的value
		//获取通信标签
		String code = jsonObject.getString("code");

		//判断通信是否成功
		if(StringUtils.equals("10000",code)) {

			//通信成功,获取业务处理结果
			Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

			//判断业务处理结果
			if (isok) {
				//从session中获取用户信息
				User sessoinUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

				User user = new User();
				user.setId(sessoinUser.getId());
				user.setName(realName);
				user.setIdCard(idCard);

				//更新用户的信息
				int modifyUserCount = userService.modifyUserById(user);

				if (modifyUserCount > 0) {
					//更新session中用户的信息
					request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserByPhone(sessoinUser.getPhone()));

					retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

				} else {
					retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
					return retMap;
				}

			} else {
				retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
				return retMap;
			}


		} else {
			//通信失败
			retMap.put(Constants.ERROR_MESSAGE,"实名认证失败");
			return retMap;
		}


		return retMap;
	}


	@RequestMapping(value = "/loan/loadStat")
	public @ResponseBody Object loadStat(HttpServletRequest request) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		//历史平均年化收益率
		Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

		//平台注册总人数
		Long allUserCount = userService.queryAllUserCount();

		//平台累计投资金额
		Double allBidMoney = bidInfoService.queryAllBidMoney();

		retMap.put("historyAverageRate",historyAverageRate);
		retMap.put("allUserCount",allUserCount);
		retMap.put("allBidMoney",allBidMoney);

		return retMap;
	}


	@RequestMapping(value = "/loan/login")
	public @ResponseBody Object login(HttpServletRequest request,
									  @RequestParam (value = "phone",required = true) String phone,
									  @RequestParam (value = "loginPassword",required = true) String loginPassword) {
		Map<String,Object> retMap = new HashMap<String,Object>();


		//进行登录(手机号，登录密码)[根据手机号和密码查询用户，更新最近登录时间] -> 返回User
		User user = userService.login(phone,loginPassword);

		//判断用户是否登录
		if (null == user) {
			retMap.put(Constants.ERROR_MESSAGE,"用户名或密码有误");
			return retMap;
		}

		//将用户的信息存放到session中
		request.getSession().setAttribute(Constants.SESSION_USER,user);

		retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

		return retMap;
	}


	@RequestMapping(value = "/loan/myCenter")
	public String myCenter(HttpServletRequest request, Model model) {

		//从session中获取用户信息
		User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);


		//根据用户标识获取帐户资金信息
		FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

		//查询参数
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("uid",sessionUser.getId());
		paramMap.put("currentPage",0);
		paramMap.put("pageSize",5);


		//根据用户标识获取最近的投资记录,显示第1页，每页显示5个
		List<BidInfo> bidInfoList = bidInfoService.queryBidInfoTopByUid(paramMap);


		//根据用户标识获取最近的充值记录，显示第1页，每页显示5个
		List<RechargeRecord> rechargeRecordList = rechargeRecoudService.queryRechargeRecordTopByUid(paramMap);


		//根据用户标识获取最近的收益记录，显示第1页，每页显示5个
		List<IncomeRecord> incomeRecordList = incomeRecordService.queryIncomeRecordTopByUid(paramMap);

		model.addAttribute("financeAccount",financeAccount);
		model.addAttribute("bidInfoList",bidInfoList);
		model.addAttribute("rechargeRecordList",rechargeRecordList);
		model.addAttribute("incomeRecordList",incomeRecordList);

		return "myCenter";
	}




}
