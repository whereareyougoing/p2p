package com.bjpowernode.config;

import lombok.*;

/**
 * @author 宋艾衡 on 2018/8/14 下午5:54
 */

@Data
public class PayConfig {

	/**
	 * 支付宝支付网关
	 */
	private String alipayGatewayUrl;
	/**
	 * 应用ID
	 */
	private String alipayAppId;
	/**
	 * 应用私钥
	 */
	private String alipayPrivateKey;
	/**
	 * 格式
	 */
	private String alipayFormat;
	/**
	 * 字符集
	 */
	private String alipayCharset;
	/**
	 * 支付宝公钥：由支付宝系统通过应用公钥生成
	 */
	private String alipayPublicKey;
	/**
	 * 签名类型：推荐使用RSA2
	 */
	private String alipaySignType;

	/**
	 * 同步返回地址
	 */
	private String alipayReturnUrl;

	/**
	 * 异步返回地址
	 */
	private String alipayNotifyUrl;



}
