package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserBid;

import java.util.List;
import java.util.Map; /**
 * @author 宋艾衡 on 2018/8/9 下午8:05
 */
public interface BidInfoService {


	Double queryAllBidMoney();

	ResultObject invest(Map<String, Object> paramMap);

	List<UserBid> queryUserBidTop();

	
}
