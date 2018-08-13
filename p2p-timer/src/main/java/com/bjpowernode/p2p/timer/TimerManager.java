package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author 宋艾衡 on 2018/8/13 下午2:41
 */

@Component
public class TimerManager {

	private Logger logger = LogManager.getLogger ( TimerManager.class );

	@Autowired
	private IncomeRecordService incomeRecordService;

	@Scheduled(cron = "0/5 * * * * *")
	public void generateIncomePlan(){
		logger.info ( "---------------生成收益开始---------------" );
		incomeRecordService.generateIncomePlan ();
		logger.info ( "---------------生成收益结束---------------" );
	}

	@Scheduled (cron = "0/5 * * * * *")
	public void generateIncomeBack(){
		logger.info ( "---------------收益返还开始---------------" );
		incomeRecordService.generateIncomeBack();
		logger.info ( "---------------收益返还结束---------------" );
	}

}
