package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author 宋艾衡 on 2018/8/10 下午11:50
 */
public class PaginationVO<T> implements Serializable {

	private Long total;

	private List<T> dataList;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
}
