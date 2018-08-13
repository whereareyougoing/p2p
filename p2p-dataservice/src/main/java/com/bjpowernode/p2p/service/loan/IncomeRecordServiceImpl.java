package com.bjpowernode.p2p.service.loan;


import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/13 下午2:51
 */
public class IncomeRecordServiceImpl implements IncomeRecordService {

	private Logger logger = LogManager.getLogger ( IncomeRecordServiceImpl.class );

	@Autowired
	private IncomeRecordMapper incomeRecordMapper;
	@Autowired
	private LoanInfoMapper loanInfoMapper;
	@Autowired
	private BidInfoMapper bidInfoMapper;
	@Autowired
	private FinanceAccountMapper financeAccountMapper;


	@Override
	public List<IncomeRecord> queryIncomeRecordTopByUid(Map<String, Object> paramMap) {
		return incomeRecordMapper.selectIncomeRecordByPage(paramMap);
	}

	@Override
	public void generateIncomePlan() {



	}


	@Override
	public void generateIncomeBack() {





	}
}
