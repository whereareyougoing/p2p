package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 宋艾衡 on 2018/8/13 上午10:37
 */

@Controller
@RequestMapping
public class BidInfoController {


	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private BidInfoService bidInfoService;


	@RequestMapping(value = "/loan/invert")
	public Object inverst(HttpServletRequest request,
						  @RequestParam(value = "loanId",required = true) Integer loanId,
						  @RequestParam(value = "bidMoney",required = true) Double bidMoney){

		Map<String,Object> retMap = Maps.newHashMap ();

		User user = (User) request.getSession ().getAttribute ( Constants.SESSION_USER );

		Map<String,Object> paramMap = Maps.newHashMap ();
		paramMap.put ( "uid",user.getId () );
		paramMap.put ( "loanId",loanId );
		paramMap.put ( "bidMoney",bidMoney );
		paramMap.put ( "phone",user.getPhone () );


		ResultObject resultObject = bidInfoService.inverst(paramMap);

		if (StringUtils.equals ( Constants.SUCCESS,resultObject.getErrorCode () )){
			retMap.put ( Constants.ERROR_MESSAGE, Constants.OK );
		}else {
			retMap.put ( Constants.ERROR_MESSAGE,"投资失败" );
			return retMap;
		}
		return retMap;
	}








}
