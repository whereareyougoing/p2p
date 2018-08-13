package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;

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
	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountMapper financeAccountMapper;

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

	@Override
	public ResultObject register(String phone, String loginPassword) {
		ResultObject resultObject = new ResultObject ();

		User user = new User ();

		user.setPhone ( phone );
		user.setLoginPassword ( loginPassword );
		user.setAddTime ( new Date () );
		user.setLastLoginTime ( new Date () );

		int insertUserCount = userMapper.insertSelective ( user );

		if (insertUserCount > 0){
			User userInfo = userMapper.selectUserByPhone ( phone );

			FinanceAccount financeAccount = new FinanceAccount ();
			financeAccount.setUid ( userInfo.getId () );
			financeAccount.setAvailableMoney ( Constants.AVAILABLE_MONEY );

			int insertFiannceCount = financeAccountMapper.insertSelective ( financeAccount );

			if (insertFiannceCount <= 0){
				resultObject.setErrorCode ( Constants.FAIL );
				resultObject.setErrorMessage ( "注册失败" );
			}
		}else {
			resultObject.setErrorCode ( Constants.FAIL );
			resultObject.setErrorMessage ( "注册失败" );
		}

		resultObject.setErrorCode ( Constants.SUCCESS );
		resultObject.setErrorMessage ( "注册成功" );
		return resultObject;
	}
}

























