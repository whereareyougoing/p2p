package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * @author 宋艾衡 on 2018/8/10 下午8:25
 */
public interface FinanceAccountService {

	/**
	 * 根据用户标识获取帐户资金信息
	 * @param uid
	 * @return
	 */
	FinanceAccount queryFinanceAccountByUid(Integer uid);

}
