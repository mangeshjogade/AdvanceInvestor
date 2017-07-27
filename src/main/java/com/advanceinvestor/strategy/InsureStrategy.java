package com.advanceinvestor.strategy;

import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.vo.TradeDecisionVO;

public class InsureStrategy extends AbstractStrategy {

	private Double lossPercentage;
	private Double profitPercentage;
	private Integer executionCount=0;

	public InsureStrategy(Double profitPercentage, Double lossPercentage) {
		super();
		this.lossPercentage = lossPercentage;
		this.profitPercentage = profitPercentage;
	}
	
	public TradeDecisionVO returnDecision(Double currentPrice, 
			Integer orderCount, 
			Integer retentionQuantity, 
			Double retentionPrice) {
		if(retentionPrice > currentPrice ){
			if((orderCount * -1) < retentionQuantity){
				if(executionCount > 3){
					lossPercentage +=0.1; 
				}
				executionCount++;
				return createBracketOrderDecisionWithPercentage(AdvanceInvestorConstants.SELL,
						orderCount+retentionQuantity, 
						retentionPrice, 
						retentionPrice+0.5, 
						(profitPercentage/100), 
						(lossPercentage/100));
			}
		}
		return null;
	}
	
	public void setLossPercentage(Double lossPercentage) {
		this.lossPercentage = lossPercentage;
	}

	public void setProfitPercentage(Double profitPercentage) {
		this.profitPercentage = profitPercentage;
	}

}
