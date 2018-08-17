package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/9 上午9:21
 */

@Controller
public class IndexController {


	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private LoanInfoService loanInfoService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private UserService userService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private BidInfoService bidInfoService;

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, Model model) {

		//历史平均年化收益率
		Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
		model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

		//平台注册总人数
		Long allUserCount = userService.queryAllUserCount();
		model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);

		//平台累计投资金额
		Double allBidMoney = bidInfoService.queryAllBidMoney();
		model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);


		//将以下方法看成是一个分页，根据产品标识获取产品信息列表(产品类型，页码，每页显示条数) -> 返回List<产品>
		//准备查询的参数
		Map<String,Object> paramMap = new HashMap<String,Object> ();
		paramMap.put("currentPage",0);//页码

		//新手宝，显示第1页，每页显示1个
		paramMap.put("productType",Constants.PRODUCT_TYPE_X);
		paramMap.put("pageSize",1);
		List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);


		//优选：显示第1页，每页显示4个
		paramMap.put("productType",Constants.PRODUCT_TYPE_U);
		paramMap.put("pageSize",4);
		List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);


		//散标：显示第1页，每页显示8个
		paramMap.put("productType",Constants.PRODUCT_TYPE_S);
		paramMap.put("pageSize",8);
		List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);

		model.addAttribute("xLoanInfoList",xLoanInfoList);
		model.addAttribute("uLoanInfoList",uLoanInfoList);
		model.addAttribute("sLoanInfoList",sLoanInfoList);


		return "index";
	}


}
