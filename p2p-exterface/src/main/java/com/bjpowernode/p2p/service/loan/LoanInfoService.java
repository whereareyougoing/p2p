package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;

import java.util.List;
import java.util.Map; /**
 * @author 宋艾衡 on 2018/8/9 上午9:26
 */
public interface LoanInfoService {


	Double queryHistoryAverageRate();

	List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

}
