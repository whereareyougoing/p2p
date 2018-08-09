package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/9 上午9:21
 */

@Controller
public class IndexController {


	@Autowired
	private LoanInfoService loanInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private BidInfoService bidInfoService;

	@RequestMapping(value = "index",method = RequestMethod.POST)
	public String index(HttpServletRequest request, Model model){

//		历史平均年化收益率
		Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
		model.addAttribute ( Constants.HISTORY_AVERAGE_RATE,historyAverageRate );

//		平台注册总人数
		Long allUserCount = userService.queryUserCount();
		model.addAttribute ( Constants.ALL_USER_COUNT,allUserCount );

//		平台总计投资金额
		Double allBidMoney = bidInfoService.queryAllBidMoney();
		model.addAttribute ( Constants.ALL_BID_MONEY,allBidMoney);

//		根据产品表示获取产品列表，分页


		return "index";
	}


}
