package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultObject;

/**
 * @author 宋艾衡 on 2018/8/9 下午8:06
 */
public interface UserService {
	Long queryUserCount();

	User queryUserPhone(String phone);

	ResultObject register(String phone, String loginPassword);

}
