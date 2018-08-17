package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserBid;

import java.util.List;
import java.util.Map; /**
 * @author 宋艾衡 on 2018/8/9 下午8:05
 */
public interface BidInfoService {


	/**
	 * 获取平台累计投资金额
	 * @return
	 */
	Double queryAllBidMoney();

	/**
	 * 根据产品标识获取该产品的所有投资记录（包含用户信息）
	 * @param loanId
	 * @return
	 */
	List<BidInfo> queryBidInfoListByLoanId(Integer loanId);

	/**
	 * 根据用户标识获取最近的投资记录
	 * @param paramMap
	 * @return
	 */
	List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap);

	/**
	 * 根据用户标识分页查询投资记录
	 * @param paramMap
	 * @return
	 */
	PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap);

	/**
	 * 用户投资
	 * @param paramMap
	 * @return
	 */
	ResultObject invest(Map<String, Object> paramMap);

	/**
	 * 获取用户投资排行榜
	 * @return
	 */
	List<UserBid> queryUserBidTop();


}
