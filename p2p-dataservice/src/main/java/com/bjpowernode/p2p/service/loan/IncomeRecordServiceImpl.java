package com.bjpowernode.p2p.service.loan;


import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.comman.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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

		//获取到已满标的产品 -> 返回List<产品列表>
		List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByProductStatus(1);


		//循环遍历获取每一个产品，获取该产品的所有投资记录 -> 返回List<投资记录列表>
		for (LoanInfo loanInfo:loanInfoList) {

			Integer productType = loanInfo.getProductType();
			Date productFullTime = loanInfo.getProductFullTime();
			Integer cycle = loanInfo.getCycle();
			Double rate = loanInfo.getRate();

			//获取该产品的所有投资记录
			List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByLoanId(loanInfo.getId());

			//循环遍历获取每一条投资记录，将它生成对应的收益记录
			for (BidInfo bidInfo:bidInfoList) {

				//生成收益记录
				IncomeRecord incomeRecord = new IncomeRecord();
				incomeRecord.setUid(bidInfo.getUid());//用户标识
				incomeRecord.setLoanId(loanInfo.getId());//投资产品的标识
				incomeRecord.setBidId(bidInfo.getId());//投资记录标识
				incomeRecord.setBidMoney(bidInfo.getBidMoney());//投资金额
				incomeRecord.setIncomeStatus(0);//收益状态：0未返还，1已返还


				//收益时间(Date) = 满标时间（Date） + 产品周期(Integer)（单位分类：天【新手宝】，月【优选和散标】）
				Date incomeDate = null;

				//收益金额 = 投资金额 * 天利率 * 投资天数;
				Double incomeMoney = null;

				//判断产品类型
				if (Constants.PRODUCT_TYPE_X == productType) {
					//新手宝
					incomeDate = DateUtils.getDateByAddDays(productFullTime,cycle);
					incomeMoney = bidInfo.getBidMoney() * (rate / 100 / DateUtils.getDaysByYear( Calendar.getInstance().get(Calendar.YEAR))) * cycle;

				} else {
					//优选或散标
					incomeDate = DateUtils.getDateByAddMonth(productFullTime,cycle);
					incomeMoney = bidInfo.getBidMoney() * (rate / 100 / DateUtils.getDaysByYear(Calendar.getInstance().get(Calendar.YEAR))) * DateUtils.getDistanceBetweenDate(incomeDate,productFullTime);
				}

				incomeMoney = Math.round(incomeMoney * Math.pow(10,2)) / Math.pow(10,2);

				incomeRecord.setIncomeDate(incomeDate);
				incomeRecord.setIncomeMoney(incomeMoney);

				int insertCount = incomeRecordMapper.insertSelective(incomeRecord);

				if (insertCount > 0) {
					logger.info("用户标识为" + bidInfo.getUid() + ",投资记录标识为" + bidInfo.getId() + ",生成收益计划成功");
				} else {
					logger.info("用户标识为" + bidInfo.getUid() + ",投资记录标识为" + bidInfo.getId() + ",生成收益计划失败");
				}

			}


			//将当前产品的状态更新为2满标且生成收益计划
			LoanInfo updateLoanInfo = new LoanInfo();
			updateLoanInfo.setId(loanInfo.getId());
			updateLoanInfo.setProductStatus(2);
			int updateCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
			if (updateCount > 0) {
				logger.info("产品标识为" + loanInfo.getId() + ",状态更新成功");
			} else {
				logger.info("产品标识为" + loanInfo.getId() + ",状态更新失败");

			}

		}

	}

	@Override
	public void generateIncomeBack() {

		//查询收益记录状态为0且收益时间与当前时间相同的收益记录
		List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeStatusAndIncomeDate(0);

		//循环遍历收益记录，将收益返还给对应的用户
		for (IncomeRecord incomeRecord:incomeRecordList) {
			Map<String,Object> paramMap = new HashMap<String,Object> ();
			paramMap.put("uid",incomeRecord.getUid());//用户标识
			paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());//收益金额
			paramMap.put("bidMoney",incomeRecord.getBidMoney());//投资金额

			//更新用户的帐户可用余额
			int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

			if (updateCount > 0) {
				//更新当前收益的状态为1
				IncomeRecord updateIncomeRecord = new IncomeRecord();
				updateIncomeRecord.setId(incomeRecord.getId());
				updateIncomeRecord.setIncomeStatus(1);
				int updateIncomeCount = incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);

				if (updateIncomeCount <= 0){
					logger.info("收益标识为" + incomeRecord.getId() + "，更新状态失败");
				}

			} else {
				logger.info("收益标识为" + incomeRecord.getId() + ",更新帐户余额失败");
			}
		}


	}
}
