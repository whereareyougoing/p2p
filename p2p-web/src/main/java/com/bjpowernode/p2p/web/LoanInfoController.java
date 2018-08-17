package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.UserBid;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/9 下午10:36
 */
@Controller
@RequestMapping("/loan/")
public class LoanInfoController {


	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private LoanInfoService loanInfoService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private BidInfoService bidInfoService;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountService financeAccountServie;

	@RequestMapping(value = "/loan/loan")
	public String loan(HttpServletRequest request, Model model,
					   @RequestParam (value = "ptype",required = false) Integer ptype,
					   @RequestParam (value = "currentPage",required = false) Integer currentPage){

		//判断是否为首页
		if (null == currentPage) {
			//默认为第1页
			currentPage = 1;
		}

		//准备分页查询的参数
		Map<String,Object> paramMap = new HashMap<String,Object> ();
		Integer pageSize = 9;
		paramMap.put("currentPage",(currentPage-1)*pageSize);
		paramMap.put("pageSize",pageSize);
		if (null != ptype) {
			paramMap.put("productType",ptype);
		}


		//分页查询产品信息列表（类型，页码，显示条数） -> 返回List<产品>，总记录数，定义一个分页查询模型对象PaginationVO
		PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

		//计算总总页数
		int totalPage = paginationVO.getTotal().intValue() / pageSize;
		int mod = paginationVO.getTotal().intValue() % pageSize;
		if (mod > 0) {
			totalPage = totalPage + 1;
		}

		model.addAttribute("totalRows",paginationVO.getTotal());
		model.addAttribute("totalPage",totalPage);
		model.addAttribute("loanInfoList",paginationVO.getDataList());
		model.addAttribute("currentPage",currentPage);
		if (null != ptype) {
			model.addAttribute("ptype",ptype);
		}

		//用户投资排行榜
		List<UserBid> userBidList = bidInfoService.queryUserBidTop();
		model.addAttribute("userBidList",userBidList);

		return "loan";
	}

	@RequestMapping(value = "/loan/loanInfo")
	public String loanInfo(HttpServletRequest request, Model model,
						   @RequestParam(value = "id",required = true) Integer id) {

		//根据产品标识获取产品详情
		LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);


		//根据产品标识获取该产品的所有投资记录
		List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByLoanId(id);

		model.addAttribute("loanInfo",loanInfo);
		model.addAttribute("bidInfoList",bidInfoList);

		//从session中获取用户信息
		User sessionUser = (User) request.getSession().getAttribute( Constants.SESSION_USER);

		//判断用户是否登录
		if (null != sessionUser) {

			//根据用户标识查询帐户资金信息
			FinanceAccount financeAccount = financeAccountServie.queryFinanceAccountByUid(sessionUser.getId());
			model.addAttribute("financeAccount",financeAccount);
		}




		return "loanInfo";
	}


}
