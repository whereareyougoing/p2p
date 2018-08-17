package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.vo.ResultObject;
import com.bjpowernode.p2p.model.vo.UserBid;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private FinanceAccountMapper financeAccountMapper;

	@Override
	public Double queryAllBidMoney() {

		//设置key的序列化方式
		redisTemplate.setKeySerializer(new StringRedisSerializer());

		//获取平台累计投资金额
		Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

		//判断是否为空
		if (null == allBidMoney) {
			//去数据库查询
			allBidMoney = bidInfoMapper.selectAllBidMoney();
			//将查询的结果存放到redis缓存中
			redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.MINUTES);

		}

		return allBidMoney;
	}


	@Override
	public List<BidInfo> queryBidInfoListByLoanId(Integer loanId) {
		return bidInfoMapper.selectBidInfoListByLoanId(loanId);
	}

	@Override
	public List<BidInfo> queryBidInfoTopByUid(Map<String, Object> paramMap) {
		return bidInfoMapper.selectBidInfoByPage(paramMap);
	}

	@Override
	public PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap) {
		PaginationVO<BidInfo> paginationVO = new PaginationVO<> ();

		paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));
		paginationVO.setDataList(bidInfoMapper.selectBidInfoByPage(paramMap));

		return paginationVO;
	}

	@Override
	public ResultObject invest(Map<String, Object> paramMap) {
		ResultObject resultObject = new ResultObject();
		resultObject.setErrorCode(Constants.SUCCESS);
		resultObject.setErrorMessage("投资成功");


		//更新产品剩余可投金额
		//超卖现象：实际销售的数量超过的库存数量
		//解决方案：数据库的乐观机制
		//获取产品版本号
		LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));

		paramMap.put("version",loanInfoDetail.getVersion());

		int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByLoanId(paramMap);

		if (updateLeftProductMoneyCount > 0) {
			//更新帐户可用余额
			int updateFinanceCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);

			if (updateFinanceCount > 0) {
				//新增投资记录
				BidInfo bidInfo = new BidInfo();
				bidInfo.setUid((Integer) paramMap.get("uid"));
				bidInfo.setLoanId((Integer) paramMap.get("loanId"));
				bidInfo.setBidMoney((Double) paramMap.get("bidMoney"));
				bidInfo.setBidTime(new Date());
				bidInfo.setBidStatus(1);

				int insertBidCount = bidInfoMapper.insertSelective(bidInfo);

				if (insertBidCount > 0) {
					LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) paramMap.get("loanId"));

					//判断产品是否满标
					if (0 == loanInfo.getLeftProductMoney()) {

						//更新产品的状态及满标时间
						LoanInfo updateLoanInfo = new LoanInfo();
						updateLoanInfo.setId(loanInfo.getId());
						updateLoanInfo.setProductStatus(1);//产品状态：0未满标，1已满标，2满标且生成收益计划
						updateLoanInfo.setProductFullTime(new Date());
						int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);

						if (updateFinanceCount <= 0) {
							resultObject.setErrorCode(Constants.FAIL);
							resultObject.setErrorMessage("投资失败");
						}

					}


					redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,paramMap.get("phone"), (Double) paramMap.get("bidMoney"));


				} else {
					resultObject.setErrorCode(Constants.FAIL);
					resultObject.setErrorMessage("投资失败");
				}

			} else {
				resultObject.setErrorCode(Constants.FAIL);
				resultObject.setErrorMessage("投资失败");
			}

		} else {
			resultObject.setErrorCode(Constants.FAIL);
			resultObject.setErrorMessage("投资失败");
		}

		return resultObject;
	}


	@Override
	public List<UserBid> queryUserBidTop() {
		List<UserBid> userBidList = new ArrayList<UserBid>();

		Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 9);

		Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();

		while (iterator.hasNext()) {
			ZSetOperations.TypedTuple<Object> next = iterator.next();

			UserBid userBid = new UserBid();
			userBid.setPhone((String) next.getValue());
			userBid.setScore(next.getScore());

			userBidList.add(userBid);
		}


		return userBidList;
	}



}

















