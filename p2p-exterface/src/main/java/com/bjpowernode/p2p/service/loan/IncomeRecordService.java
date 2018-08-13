package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/13 下午2:50
 */
public interface IncomeRecordService {

	/**
	 * 根据用户标识查询最近的收益
	 * @param paramMap
	 * @return
	 */
	List<IncomeRecord> queryIncomeRecordTopByUid(Map<String, Object> paramMap);

	/**
	 * 生成收益计划
	 */
	void generateIncomePlan();

	/**
	 * 收益返还
	 */
	void generateIncomeBack();

}
