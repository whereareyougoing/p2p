package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/13 下午10:17
 */

@Service("rechargeRecoudServiceImpl")
public class RechargeRecoudServiceImpl implements RechargeRecoudService {


	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private RechargeRecordMapper rechargeRecordMapper;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountMapper financeAccountMapper;

	@Override
	public List<RechargeRecord> queryRechargeRecordTopByUid(Map<String, Object> paramMap) {
		return rechargeRecordMapper.selectRechargeRecordByPage(paramMap);
	}

	@Override
	public PaginationVO<RechargeRecord> queryRechargeRecordByPage(Map<String, Object> paramMap) {
		PaginationVO<RechargeRecord> paginationVO = new PaginationVO<>();

		paginationVO.setTotal(rechargeRecordMapper.selectTotal(paramMap));
		paginationVO.setDataList(rechargeRecordMapper.selectRechargeRecordByPage(paramMap));

		return paginationVO;

	}

	@Override
	public int addRechargeRecord(RechargeRecord rechargeRecord) {
		return rechargeRecordMapper.insertSelective(rechargeRecord);
	}

	@Override
	public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
		return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
	}

	@Override
	public int recharge(Map<String, Object> paramMap) {
		//更新帐户的可用余额
		int updateFinanceCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
		if (updateFinanceCount > 0) {
			//更新充值记录的状态
			RechargeRecord rechargeRecord = new RechargeRecord();
			rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
			rechargeRecord.setRechargeStatus("1");
			int updateRechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);

			if (updateRechargeCount <= 0) {
				return 0;
			}


		} else {
			return 0;
		}
		return 1;
	}
}
