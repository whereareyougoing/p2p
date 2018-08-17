package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.comman.util.DateUtils;
import com.bjpowernode.p2p.comman.util.HttpClientUtils;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.OnlyNumberService;
import com.bjpowernode.p2p.service.loan.RechargeRecoudService;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/14 下午2:41
 */
@Controller
@RequestMapping
public class RechargeRecordController {

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private RechargeRecoudService rechargeRecordService;
	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private OnlyNumberService onlyNumberService;


	@RequestMapping(value = "/loan/myRecharge")
	public String myRecharge(HttpServletRequest request, Model model,
							 @RequestParam(value = "currentPage", required = true) Integer currentPage) {
		//判断是否为第1页
		if (null == currentPage) {
			currentPage = 1;
		}

		//从session中获取用户信息
		User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

		//准备分页查询参数
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("uid",sessionUser.getId());
		int pageSize = 10;
		paramMap.put("currentPage",(currentPage-1)*pageSize);
		paramMap.put("pageSize",pageSize);

		//根据用户标识分页查询投资记录（用户标识，页码，每页显示条数） -> 返回PaginationVO
		PaginationVO<RechargeRecord> paginationVO = rechargeRecordService.queryRechargeRecordByPage(paramMap);

		//计算总页数
		int totalPage = paginationVO.getTotal().intValue() / pageSize;
		int mod = paginationVO.getTotal().intValue() % pageSize;
		if (mod > 0) {
			totalPage = totalPage + 1;
		}

		model.addAttribute("totalRows",paginationVO.getTotal());
		model.addAttribute("totalPage",totalPage);
		model.addAttribute("rechargeRecordList",paginationVO.getDataList());
		model.addAttribute("currentPage",currentPage);

		return "myRecharge";
	}


	/**
	 * 支付宝支付
	 * @param request
	 * @param model
	 * @param rechargeMoney
	 * @return
	 */

	@RequestMapping(value = "/loan/toAlipayRecharge")
	public String toAliPayRecharge(HttpServletRequest request, Model model,
								   @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney){
		System.out.println ("---------toAlipay-------");

		User user = (User) request.getSession ().getAttribute ( Constants.SESSION_USER );

//		生成一个全局的唯一单号，时间戳+redis全局唯一数字
		String rechargeNo = DateUtils.getTimeStamp () + onlyNumberService.getOnlyNumber ();

//		生成充值记录
		RechargeRecord rechargeRecord = new RechargeRecord ();
		rechargeRecord.setUid ( user.getId () );
		rechargeRecord.setRechargeMoney ( rechargeMoney );
		rechargeRecord.setRechargeNo ( rechargeNo );
		rechargeRecord.setRechargeTime ( new Date () );
		rechargeRecord.setRechargeDesc ( "支付宝充值" );
		rechargeRecord.setRechargeStatus ( "0" );//0充值中，1充值成功

		int addRechargeCount = rechargeRecordService.addRechargeRecord ( rechargeRecord );

		if (addRechargeCount > 0){
			model.addAttribute("p2p_pay_alipay_url","http://localhost:9090/pay/api/alipay");
			model.addAttribute("rechargeNo",rechargeNo);
			model.addAttribute("rechargeMoney",rechargeMoney);
			model.addAttribute("subject","支付宝充值");
			model.addAttribute("body","支付宝充值");
		}else {
			model.addAttribute ( "trade_msg", "充值失败" );
			return "toRechargeBack";
		}

		return "toAlipay";
	}


	/**
	 * 支付宝支付回调
	 */
	@RequestMapping(value = "/loan/alipayBack")
	public String alipayBack(HttpServletRequest request, Model model,
							 @RequestParam(value = "out_trade_no", required = true) String out_trade_no,
							 @RequestParam(value = "total_amount", required = true) String total_amount,
							 @RequestParam(value = "signVerified", required = true) String signVerified) {
		System.out.println ( "========alipayBack========" );

//		验证结果
		if (StringUtils.equals ( Constants.SUCCESS,signVerified )){
			Map<String, Object> paramMap = Maps.newHashMap ();
			paramMap.put ( "out_trade_no", out_trade_no );
//			调用pay工程的订单查询接口 -> 相应json格式的字符串
			String jsonResult = HttpClientUtils.doPost ( "http://localhost:9090/pay/api/alipayQuery", paramMap );

//			解析json格式字符串
//			将json格式的字符串转换为json对象
			JSONObject jsonObject = JSONObject.parseObject ( jsonResult );
//			获取指定的key和value
			JSONObject tradeQueryResponse = jsonObject.getJSONObject ( "alipay_trade_query_response" );
//			获取通信标识
			String code = tradeQueryResponse.getString ( "code" );

			if (StringUtils.equals ( "10000",code )){
//				获取订单的处理结果
				String trade_status = tradeQueryResponse.getString ( "trade_status" );

				/*WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款*/

				if ("TRADE_CLOSE".equals ( trade_status )) {
//					更新充值记录为2，充值失败
					RechargeRecord updateRecharge = new RechargeRecord ();
					updateRecharge.setRechargeNo ( out_trade_no );
					updateRecharge.setRechargeStatus ( "2" );
					int updateRechargeRecordCount = rechargeRecordService.modifyRechargeRecordByRechargeNo ( updateRecharge );

					if (updateRechargeRecordCount <= 0) {
						model.addAttribute ( "trade_msg", "通信繁忙" );
						return "toRechargeBack";
					}
				}

				if ("TRADE_SUCCESS".equals ( trade_status )){
//					从session里获取用户信息
					User sessionUser = (User) request.getSession ().getAttribute ( Constants.SESSION_USER );

					//准备充值参数
					paramMap.put("uid",sessionUser.getId());
					paramMap.put("rechargeMoney",total_amount);
					paramMap.put("rechargeNo",out_trade_no);

					//给用户充值（用户标识，充值金额,充值订单号）【给用户帐户余额加钱，更新充值记录状态为1】
					int rechargeCount = rechargeRecordService.recharge(paramMap);

					if (rechargeCount <= 0) {
						model.addAttribute("trade_msg","通信繁忙");
						return "toRechargeBack";
					}

				}
			}else {
				model.addAttribute("trade_msg","通信繁忙");
				return "toRechargeBack";
			}
		}else {
			model.addAttribute("trade_msg","验证签名失败");
			return "toRechargeBack";
		}

		return "redirect:/loan/myRecharge";

	}

	/**
	 * 微信支付
	 * @param request
	 * @param model
	 * @param rechargeMoney
	 * @return
	 */

	@RequestMapping(value = "/loan/toAlipayRecharge")
	public String toWxPayRecharge(HttpServletRequest request, Model model,
								  @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {

		System.out.println ( "---------toWxPay-------" );

		//从session中获取用户信息
		User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

		//生成一个全局唯一的充值订单号 = 时间戳 + redis全局唯一数字
		String rechargeNo = DateUtils.getTimeStamp() + onlyNumberService.getOnlyNumber();

		//生成充值记录
		RechargeRecord rechargeRecord = new RechargeRecord();
		rechargeRecord.setUid(sessionUser.getId());
		rechargeRecord.setRechargeNo(rechargeNo);
		rechargeRecord.setRechargeMoney(rechargeMoney);
		rechargeRecord.setRechargeTime(new Date());
		rechargeRecord.setRechargeStatus("0");//0充值中，1充值成功，2充值失败
		rechargeRecord.setRechargeDesc("微信充值");

		int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);

		if (addRechargeCount > 0) {
			model.addAttribute("rechargeNo",rechargeNo);
			model.addAttribute("rechargeMoney",rechargeMoney);
			model.addAttribute("rechargeTime",new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss").format(new Date()));

		} else {
			model.addAttribute("trade_msg","充值失败");
			return "toRechargeBack";
		}

		return "showQRCode";
	}


	@RequestMapping(value = "/loan/generateQRCode")
	public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
							   @RequestParam(value = "rechargeNo", required = true) String rechargeNo,
							   @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) throws WriterException, IOException {

		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("body","微信支付");
		paramMap.put("out_trade_no",rechargeNo);
		paramMap.put("total_fee",rechargeMoney);

		//调用pay统一下单API接口 -> 返回参数，参数中包含code_url
		String jsonResult = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);

		//解析json格式的字符串
		JSONObject jsonObject = JSONObject.parseObject(jsonResult);

		//获取通信标识
		String return_code = jsonObject.getString("return_code");

		if (StringUtils.equals(Constants.SUCCESS,return_code)) {

			//获取业务处理标识
			String result_code = jsonObject.getString("result_code");

			if (StringUtils.equals(Constants.SUCCESS,result_code)) {

				//生成二维码图片，首先我得获取到code_url
				String code_url = jsonObject.getString("code_url");

				//将code_url生成二维码图片
				int width = 200;
				int hight = 200;

				Map<EncodeHintType,Object> hintMap = new HashMap<EncodeHintType, Object> ();
				hintMap.put( EncodeHintType.CHARACTER_SET,"UTF-8");

				//创建一个矩阵对象
				BitMatrix bitMatrix = new MultiFormatWriter ().encode(code_url, BarcodeFormat.QR_CODE,width,hight,hintMap);

				//获取输出流对象
				OutputStream outputStream = response.getOutputStream();

				//将矩阵对象转换为流
				MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);

				outputStream.flush();
				outputStream.close();


			} else {
				response.sendRedirect(request.getContextPath() + "/toRecharge.jsp");
			}


		} else {
			response.sendRedirect(request.getContextPath() + "/toRecharge.jsp");
		}


	}


}
