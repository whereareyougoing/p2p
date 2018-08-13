package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;

/**
 * @author 宋艾衡 on 2018/8/13 下午9:12
 */
public class UserBid implements Serializable {

	/**
	 * 用户的手机号
	 */
	private String phone;

	/**
	 * 累计投资金额
	 */
	private Double score;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}
