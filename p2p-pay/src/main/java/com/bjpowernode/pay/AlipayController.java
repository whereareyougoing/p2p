package com.bjpowernode.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.config.PayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ClassName:AlipayController
 * Package:com.bjpowernode.pay
 * Description:
 *
 * @Date:2018/8/14 15:05
 * @Author:guoxin@bjpowernode.com
 */
@Controller
public class AlipayController {

    @Autowired
    private PayConfig payConfig;

    @RequestMapping(value = "/api/alipay")
    public String alipay(HttpServletRequest request, Model model,
                         @RequestParam (value = "out_trade_no",required = true) String out_trade_no,//商户订单号，商户网站订单系统中唯一订单号，必填
                         @RequestParam (value = "total_amount",required = true) Double total_amount,//付款金额，必填
                         @RequestParam (value = "subject",required = true) String subject, //订单名称，必填
                         @RequestParam (value = "body",required = true) String body) throws AlipayApiException {//商品描述

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                payConfig.getAlipayGatewayUrl(),
                payConfig.getAlipayAppId(),
                payConfig.getAlipayPrivateKey(),
                payConfig.getAlipayFormat(),
                payConfig.getAlipayCharset(),
                payConfig.getAlipayPublicKey(),
                payConfig.getAlipaySignType());

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(payConfig.getAlipayReturnUrl());//同步返回地址
        alipayRequest.setNotifyUrl(payConfig.getAlipayNotifyUrl());//异步返回地址



        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        model.addAttribute("result",result);

        System.out.println("result====" + result);

        return "toAlipay";
    }

    @RequestMapping(value = "/api/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model) throws UnsupportedEncodingException, AlipayApiException {

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();

        //获取支付宝GET返回的参数
        Map<String,String[]> requestParams = request.getParameterMap();

        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                payConfig.getAlipayPublicKey(),
                payConfig.getAlipayCharset(),
                payConfig.getAlipaySignType());

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            model.addAttribute("signVerified","SUCCESS");
            model.addAttribute("paramMap",params);

        }else {

            model.addAttribute("signVerified","FAIL");
        }

        model.addAttribute("pay_p2p_return_url","http://localhost:8080/p2p/loan/alipayBack");

        //——请在这里编写您的程序（以上代码仅作参考）——
        return "toP2P";
    }


    @RequestMapping(value = "/api/alipayQuery")
    public @ResponseBody Object alipayQuery(HttpServletRequest request,
                                            @RequestParam (value = "out_trade_no",required = true) String out_trade_no) throws AlipayApiException {

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                payConfig.getAlipayGatewayUrl(),
                payConfig.getAlipayAppId(),
                payConfig.getAlipayPrivateKey(),
                payConfig.getAlipayFormat(),
                payConfig.getAlipayCharset(),
                payConfig.getAlipayPublicKey(),
                payConfig.getAlipaySignType());

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();


        return result;
    }
}
