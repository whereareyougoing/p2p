package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @author 宋艾衡 on 2018/8/9 下午8:07
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {


	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private UserMapper userMapper;

	@Override
	public Long queryUserCount() {
		redisTemplate.setKeySerializer ( new StringRedisSerializer (  ) );

		BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps ( Constants.ALL_USER_COUNT );

		Long allUserCount = (Long) boundValueOperations.get ();

		if (allUserCount == null) {
			allUserCount = userMapper.selectAllUserCount();
			boundValueOperations.set ( Constants.ALL_USER_COUNT,allUserCount );
		}
		return allUserCount;
	}

	@Override
	public User queryUserPhone(String phone) {
		return userMapper.selectUserByPhone(phone);
	}
}
