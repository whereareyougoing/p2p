package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 宋艾衡 on 2018/8/10 下午8:34
 */

@Service("financeAccountServiceImpl")
public class FinanceAccountServiceImpl implements FinanceAccountService {


	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountMapper financeAccountMapper;


	@Override
	public FinanceAccount queryFinanceAccountByUid(Integer uid) {
		return financeAccountMapper.selectFinanceAccountByUid(uid);
	}


}
