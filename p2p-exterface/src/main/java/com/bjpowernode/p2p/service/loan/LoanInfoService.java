package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map; /**
 * @author 宋艾衡 on 2018/8/9 上午9:26
 */
public interface LoanInfoService {


	/**
	 * 获取历史平均年化收益率
	 * @return
	 */
	Double queryHistoryAverageRate();

	/**
	 * 根据产品类型查询产品信息列表
	 * @param paramMap
	 * @return
	 */
	List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

	/**
	 * 分页查询产品信息列表
	 * @param paramMap
	 * @return
	 */
	PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

	/**
	 * 根据产品标识获取产品详情
	 * @param id
	 * @return
	 */
	LoanInfo queryLoanInfoById(Integer id);

}
