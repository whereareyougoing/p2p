package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IncomeRecordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int insert(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int insertSelective(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    IncomeRecord selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int updateByPrimaryKeySelective(IncomeRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_income_record
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int updateByPrimaryKey(IncomeRecord record);

	/**
	 * 根据用户标识分页查询收益记录
	 * @param paramMap
	 * @return
	 */
	List<IncomeRecord> selectIncomeRecordByPage(Map<String, Object> paramMap);

	/**
	 * 根据收益状态且收益时间与当前时间相同的收益记录
	 * @param incomeStatus
	 * @return
	 */
	List<IncomeRecord> selectIncomeRecordByIncomeStatusAndIncomeDate(Integer incomeStatus);

}