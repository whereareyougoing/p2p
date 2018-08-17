package com.bjpowernode.pay;

import com.bjpowernode.p2p.comman.util.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/16 下午2:50
 */

@Controller
public class WxpayController {


	@RequestMapping(value = "/api/wxpay")
	@ResponseBody
	public Object wxpay(HttpServletRequest request,
						@RequestParam(value = "body", required = true) String body,
						@RequestParam(value = "out_trade_no", required = true) String out_trade_no,
						@RequestParam(value = "total_fee", required = true) Double total_fee) throws Exception {
		//创建一个请求参数Map
		Map<String, String> requestDataMap = new HashMap<String, String> ();

		requestDataMap.put ( "appid", "wx8a3fcf509313fd74" );
		requestDataMap.put ( "mch_id", "1361137902" );
		requestDataMap.put ( "nonce_str", WXPayUtil.generateNonceStr () );
		requestDataMap.put ( "body", body );
		requestDataMap.put ( "out_trade_no", out_trade_no );
		BigDecimal bigDecimal = new BigDecimal ( total_fee );
		BigDecimal multiply = bigDecimal.multiply ( new BigDecimal ( 100 ) );
		requestDataMap.put ( "total_fee", multiply.toString () );
		requestDataMap.put ( "spbill_create_ip", "127.0.0.1" );
		requestDataMap.put ( "notify_url", "http://localhost:9090/pay/api/wxpayNotify" );
		requestDataMap.put ( "trade_type", "NATIVE" );
		requestDataMap.put ( "product_id", out_trade_no );

		//生成签名值
		String signature = WXPayUtil.generateSignature ( requestDataMap, "367151c5fd0d50f1e34a68a802d6bbca" );
		requestDataMap.put ( "sign", signature );

		//xml格式的请求上传参数
		String requestDataXml = WXPayUtil.mapToXml ( requestDataMap );

		//调用微信统一下单API接口 -> 返回xml格式的字符串
		String responseDataXml = HttpClientUtils.doPostByXml ( "https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml );

		//将响应的xml格式的字符串转换为Map集合
		Map<String, String> responseDataMap = WXPayUtil.xmlToMap ( responseDataXml );

		return responseDataMap;
	}
}
