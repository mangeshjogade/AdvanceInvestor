package com.advanceinvestor.strategy;

import java.util.ArrayList;
import java.util.List;

import com.advanceinvestor.vo.InvestmentDetailVO;

public class Portfolio {
	private List<InvestmentDetailVO> investmentList = new ArrayList<InvestmentDetailVO>();
	public List<InvestmentDetailVO> getInvestmentList() {
		return investmentList;
	}
}
