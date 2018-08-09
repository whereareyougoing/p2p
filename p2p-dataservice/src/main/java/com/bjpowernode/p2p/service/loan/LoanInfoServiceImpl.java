package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;

import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * @author 宋艾衡 on 2018/8/9 上午9:26
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;
	@Autowired
	private LoanInfoMapper loanInfoMapper;


	@Override
	public Double queryHistoryAverageRate() {


		Double historyAverageRate = (Double) redisTemplate.opsForValue ().get ( Constants.HISTORY_AVERAGE_RATE );
		if (historyAverageRate == null){
			historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
			redisTemplate.opsForValue ().set ( Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES );
		}

		return historyAverageRate;

	}
}
