package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserBid;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 宋艾衡 on 2018/8/9 下午8:06
 */

@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private BidInfoMapper bidInfoMapper;

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private LoanInfoMapper loanInfoMapper;

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

	@Override
	public ResultObject invest(Map<String, Object> paramMap) {
//		更新产品剩余可投金额
//		超卖现象：解决方法数据库乐观锁
//		获得产品版本号

		ResultObject resultObject = new ResultObject ();
		resultObject.setErrorCode(Constants.SUCCESS);
		resultObject.setErrorMessage("投资成功");

		LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey ( (Integer) paramMap.get ( "loanId" ) );

		int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByLoanId(paramMap);

		if (updateLeftProductMoneyCount > 0){
			int updataFinanceCount = FinanceAccountMapper.updateFinanceAccountByBid(paramMap);

			if (updataFinanceCount > 0){
//				新增投资记录
				BidInfo bidInfo = new BidInfo ();
				bidInfo.setUid ( (Integer) paramMap.get ( "uid" ) );
				bidInfo.setLoanId ( (Integer) paramMap.get ( "loanId" ) );
				bidInfo.setBidMoney ( (Double) paramMap.get ( "bidMoney" ) );
				bidInfo.setBidTime ( new Date () );
				bidInfo.setBidStatus ( 1 );

				int insertBidCount = bidInfoMapper.insertSelective ( bidInfo );
				if (insertBidCount > 0){
					LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey ( (Integer) paramMap.get ( "loanId" ) );
					if (0 == loanInfo.getProductMoney ()){

//						更新产品状态和满标时间
						LoanInfo updateLoanInfo = new LoanInfo ();
						updateLoanInfo.setId ( loanInfo.getId () );
						updateLoanInfo.setProductStatus ( 1 );
						updateLoanInfo.setProductFullTime ( new Date (  ) );
						int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective ( updateLoanInfo );

						if (updateLoanInfoCount <= 0){
							resultObject.setErrorMessage ( Constants.FAIL );
							resultObject.setErrorMessage ( "投资失败" );
						}
					}

					redisTemplate.opsForZSet ().incrementScore ( Constants.INVEST_TOP, paramMap.get ( "phone" )
							, (Double) paramMap.get ( "bidMoney" ) );
				} else {
					resultObject.setErrorCode ( Constants.FAIL );
					resultObject.setErrorMessage ( "投资失败" );
				}
			}else {
				resultObject.setErrorCode ( Constants.FAIL );
				resultObject.setErrorMessage ( "投资失败" );
			}
			resultObject.setErrorCode ( Constants.FAIL );
			resultObject.setErrorMessage ( "投资失败" );
		}
		return resultObject;
	}


	@Override
	public List<UserBid> queryUserBidTop() {
		List<UserBid> userBidList = Lists.newArrayList();
		Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet ()
				.reverseRangeWithScores ( Constants.INVEST_TOP, 0, 9 );
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator ();
		while (iterator.hasNext () ){
			ZSetOperations.TypedTuple<Object> next = iterator.next ();

			UserBid userBid = new UserBid ();
			userBid.setPhone ( (String) next.getValue () );
			userBid.setScore ( next.getScore () );

			userBidList.add ( userBid );
		}

		return userBidList;
	}



}

















