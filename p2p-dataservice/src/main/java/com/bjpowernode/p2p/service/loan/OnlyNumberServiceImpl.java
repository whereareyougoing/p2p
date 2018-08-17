package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 宋艾衡 on 2018/8/14 下午2:54
 */
@Service("onlyNumberServiceImpl")
public class OnlyNumberServiceImpl implements OnlyNumberService {


	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;



	@Override
	public Long getOnlyNumber() {
		return redisTemplate.opsForValue().increment( Constants.ONLY_NUMBER,1);
	}

}
