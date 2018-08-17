package com.bjpowernode.p2p.service.loan;

/**
 * @author 宋艾衡 on 2018/8/14 下午2:54
 */
public interface OnlyNumberService {
	/**
	 * 获取redis全局唯一数字
	 * @return
	 */
	Long getOnlyNumber();
}
