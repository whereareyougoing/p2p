package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.comman.constant.Constants;

import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 宋艾衡 on 2018/8/9 上午9:26
 */
@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

	@Autowired
	private RedisTemplate<Object,Object> redisTemplate;
	@Autowired
	@SuppressWarnings ( "SpringJavaAutowiringInspection" )
	private LoanInfoMapper loanInfoMapper;


	@Override
	public Double queryHistoryAverageRate() {
		//首先去redis缓存中查询，判断是否有值，有：直接使用，没有：去数据库查询并存放到redis缓存中，设置失效时间
		//好处：减少对数据库访问，提升了系统性能

		//从redis缓存中获取该值
		Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

		//判断是否为空
		if (null == historyAverageRate) {
			//为空，去数据库查询
			historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
			//存放到redis缓存中
			redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES);
		}



		return historyAverageRate;
	}


	@Override
	public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
		return loanInfoMapper.selectLoanInfoByPage(paramMap);
	}

	@Override
	public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
		PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();

		paginationVO.setTotal(loanInfoMapper.selectTotal(paramMap));
		paginationVO.setDataList(loanInfoMapper.selectLoanInfoByPage(paramMap));


		return paginationVO;
	}


	@Override
	public LoanInfo queryLoanInfoById(Integer id) {
		return loanInfoMapper.selectByPrimaryKey(id);
	}
}
