package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
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

	public String loan(HttpSession session, Model model,
					   @RequestParam(value = "ptype",required = false) Integer ptype,
					   @RequestParam(value = "currentType",required = false)Integer currentPage){

		if (currentPage == null) {
			currentPage = 1;
		}

		Map<String,Object> paramMap = Maps.newHashMap ();
		Integer pageSize = 9;
		paramMap.put ( "currentPage",(currentPage-1)*pageSize );
		paramMap.put ( "pageSize",pageSize );
		if (ptype == null) {
			paramMap.put ( "productType",ptype );
		}




	}


}
