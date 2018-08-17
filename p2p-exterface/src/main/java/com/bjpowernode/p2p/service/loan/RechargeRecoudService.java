package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/13 下午10:16
 */
public interface RechargeRecoudService {

	/**
	 * 根据用户标识获取最近的充值记录
	 * @param paramMap
	 * @return
	 */
	List<RechargeRecord> queryRechargeRecordTopByUid(Map<String, Object> paramMap);

	/**
	 * 根据用户标识分页查询充值记录
	 * @param paramMap
	 * @return
	 */
	PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap);

	/**
	 * 新增充值记录
	 * @param rechargeRecord
	 * @return
	 */
	int addRechargeRecord(RechargeRecord rechargeRecord);

	/**
	 * 根据充值订单号更新充值记录
	 * @param rechargeRecord
	 * @return
	 */
	int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

	/**
	 * 用户充值
	 * @param paramMap
	 * @return
	 */
	int recharge(Map<String, Object> paramMap);


}
