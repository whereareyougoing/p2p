package com.bjpowernode.p2p.config;

/**
 * @author 宋艾衡 on 2018/8/17 上午12:29
 */
public class Config {
	/**
	 * 实名认证的key
	 */
	private String realNameAppkey;

	/**
	 * 实名认证的URL
	 */
	private String realNameUrl;


	public String getRealNameAppkey() {
		return realNameAppkey;
	}

	public void setRealNameAppkey(String realNameAppkey) {
		this.realNameAppkey = realNameAppkey;
	}

	public String getRealNameUrl() {
		return realNameUrl;
	}

	public void setRealNameUrl(String realNameUrl) {
		this.realNameUrl = realNameUrl;
	}
}
