package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @author 宋艾衡 on 2018/8/9 下午8:06
 */

@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;

	@Autowired
	private BidInfoMapper bidInfoMapper;

	@Override
	public Double queryAllBidMoney() {

//		设置key的序列化方式
		redisTemplate.setKeySerializer ( new StringRedisSerializer (  ) );

		Double allBidMoney = (Double) redisTemplate.opsForValue ().get ( Constants.ALL_BID_MONEY );
		if (allBidMoney == null) {
			allBidMoney = bidInfoMapper.selectAllBidMoney();
			redisTemplate.opsForValue ().set ( Constants.ALL_BID_MONEY,allBidMoney );
		}

		return allBidMoney;
	}



}
