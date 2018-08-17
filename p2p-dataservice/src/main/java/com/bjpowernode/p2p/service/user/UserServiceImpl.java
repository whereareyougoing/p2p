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
import java.util.concurrent.TimeUnit;

/**
 * @author 宋艾衡 on 2018/8/9 下午8:07
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {


	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspaction" )
	private UserMapper userMapper;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountMapper financeAccountMapper;

	@Override
	public Long queryAllUserCount() {
		//修改redis中key值序列化方式
		redisTemplate.setKeySerializer(new StringRedisSerializer());


		//从redis缓存中查询该值，有：直接使用，没有：去数据库查询，并存放到redis中

		//获取指定key的操作对象
		BoundValueOperations<Object, Object> boundValueOps = redisTemplate.boundValueOps(Constants.ALL_USER_COUNT);

		//从该key的操作对象中获取该key所对应的value
		Long allUserCount = (Long) boundValueOps.get();

		//判断是否有值
		if (null == allUserCount) {
			//查询数据库
			allUserCount = userMapper.selectAllUserCount();
			boundValueOps.set(allUserCount,15, TimeUnit.MINUTES);
		}


		return allUserCount;
	}


	@Override
	public User queryUserByPhone(String phone) {
		return userMapper.selectUserByPhone(phone);
	}


	@Override
	public ResultObject register(String phone, String loginPassword) {
		ResultObject resultObject = new ResultObject();
		resultObject.setErrorCode(Constants.SUCCESS);
		resultObject.setErrorMessage("注册成功");


		//新增用户
		User user = new User();
		user.setPhone(phone);
		user.setLoginPassword(loginPassword);
		user.setAddTime(new Date());
		user.setLastLoginTime(new Date());
		int insertUserCount = userMapper.insertSelective(user);

		if (insertUserCount > 0) {
			//根据手机号查询用户信息
			User userInfo = userMapper.selectUserByPhone(phone);
			//开立帐户
			FinanceAccount financeAccount = new FinanceAccount();
			financeAccount.setUid(userInfo.getId());
			financeAccount.setAvailableMoney(888.0);
			int insertFiannceCount = financeAccountMapper.insertSelective(financeAccount);

			if (insertFiannceCount <= 0) {
				resultObject.setErrorCode(Constants.FAIL);
				resultObject.setErrorMessage("注册失败");
			}

		} else {
			resultObject.setErrorCode(Constants.FAIL);
			resultObject.setErrorMessage("注册失败");
		}


		return resultObject;
	}

	@Override
	public int modifyUserById(User user) {
		return userMapper.updateByPrimaryKeySelective(user);
	}

	@Override
	public User login(String phone, String loginPassword) {

		//根据用户手机号和密码查询用户信息
		User user = userMapper.selectUserByPhoneAndLoginPassword(phone,loginPassword);

		//判断用户是否存在
		if (null != user) {
			User updateUser = new User();
			updateUser.setId(user.getId());
			updateUser.setLastLoginTime(new Date());

			//更新最近登录时间
			userMapper.updateByPrimaryKeySelective(updateUser);
		}



		return user;
	}
}

























