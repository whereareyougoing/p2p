package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int insert(LoanInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int insertSelective(LoanInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    LoanInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int updateByPrimaryKeySelective(LoanInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int updateByPrimaryKeyWithBLOBs(LoanInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table b_loan_info
     *
     * @mbggenerated Wed Aug 08 16:37:15 CST 2018
     */
    int updateByPrimaryKey(LoanInfo record);

	Double selectHistoryAverageRate();

	List<LoanInfo> selectLoanInfoByPage(Map<String, Object> paramMap);

}