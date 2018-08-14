package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.service.loan.RechargeRecoudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 宋艾衡 on 2018/8/14 下午2:41
 */
@Controller
@RequestMapping
public class RechargeRecordController {

	@Autowired
	private RechargeRecoudService rechargeRecoudService;

	@Autowired
	private

	@RequestMapping(value = "/loan/toAlipayRecharge")
	public String toAlipayRecharge(HttpServletRequest request, Model model,
								   @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney){

	}


}
