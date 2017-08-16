package com.advanceinvestor.strategy;

import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.vo.TradeDecisionVO;

public abstract class AbstractStrategy {
	
	protected TradeDecisionVO createTslBracketOrderDecision(String order, Integer quantity,
			Double price, Double triggerPrice, Double bookProfitPrice,Double stopLossPrice,Double limitLossTo,
			String startTrailingLossAt, Double startTrailingLossAtPrice){
		TradeDecisionVO tradeDecisionVO = new TradeDecisionVO();
		tradeDecisionVO.setOrder(order);
		tradeDecisionVO.setOrderType(AdvanceInvestorConstants.BRACKET_TSL_ORDER);
		tradeDecisionVO.setOrderQuantity(quantity);
		tradeDecisionVO.setPrice(price);
		tradeDecisionVO.setTriggerPrice(triggerPrice);
		tradeDecisionVO.setBookProfitPrice(bookProfitPrice);
		tradeDecisionVO.setStopLossPrice(stopLossPrice);
		tradeDecisionVO.setLimitLossTo(limitLossTo);
		tradeDecisionVO.setStartTrailingLossAt(startTrailingLossAt);
		tradeDecisionVO.setStartTrailingLossAtPrice(startTrailingLossAtPrice);
		return tradeDecisionVO; 
	}
	
	protected TradeDecisionVO createTslBracketOrderDecisionWithPercentage(String order, Integer quantity,
			Double price, Double triggerPrice, Double profitPercentage,Double lossPercentage,
			String startTrailingLossAt, Double startTrailingLossAtPrice){
		if (order.equals(AdvanceInvestorConstants.BUY))
			return createTslBracketOrderDecision(order,quantity, price, triggerPrice, price+price*profitPercentage, price-((price+5)*lossPercentage), price-price*lossPercentage,startTrailingLossAt,startTrailingLossAtPrice);
		else if (order.equals(AdvanceInvestorConstants.SELL))
			return createTslBracketOrderDecision(order,quantity, price, triggerPrice, price-price*profitPercentage, price+((price-5)*lossPercentage), price+price*lossPercentage,startTrailingLossAt,startTrailingLossAtPrice);
		return null;
	}
	
	
	
	protected TradeDecisionVO createBracketOrderDecision(String order, Integer quantity,
			Double price, Double triggerPrice, Double bookProfitPrice,Double stopLossPrice,Double limitLossTo){
		TradeDecisionVO tradeDecisionVO = new TradeDecisionVO();
		tradeDecisionVO.setOrder(order);
		tradeDecisionVO.setOrderType(AdvanceInvestorConstants.BRACKET_ORDER);
		tradeDecisionVO.setOrderQuantity(quantity);
		tradeDecisionVO.setPrice(price);
		tradeDecisionVO.setTriggerPrice(triggerPrice);
		tradeDecisionVO.setBookProfitPrice(bookProfitPrice);
		tradeDecisionVO.setStopLossPrice(stopLossPrice);
		tradeDecisionVO.setLimitLossTo(limitLossTo);
		return tradeDecisionVO; 
	}
	
	protected TradeDecisionVO createBracketOrderDecisionWithPercentage(String order, Integer quantity,
			Double price, Double triggerPrice, Double profitPercentage,Double lossPercentage){
		if (order.equals(AdvanceInvestorConstants.BUY))
			return createBracketOrderDecision(order,quantity, price, triggerPrice, price+price*profitPercentage, price-((price)*lossPercentage-0.4), price-price*lossPercentage);
		else if (order.equals(AdvanceInvestorConstants.SELL))
			return createBracketOrderDecision(order,quantity, price, triggerPrice, price-price*profitPercentage, price+((price)*lossPercentage-0.4), price+price*lossPercentage);
		return null;
	}
	
	protected TradeDecisionVO createStopLossOrderDecision(String order, Integer quantity,
			Double triggerPrice,Double price){
		TradeDecisionVO tradeDecisionVO = new TradeDecisionVO();
		tradeDecisionVO.setOrder(order);
		tradeDecisionVO.setOrderType(AdvanceInvestorConstants.STOP_LOSS_ORDER);
		tradeDecisionVO.setOrderQuantity(quantity);
		tradeDecisionVO.setPrice(price);
		tradeDecisionVO.setTriggerPrice(triggerPrice);
		return tradeDecisionVO; 
	}
	
	protected TradeDecisionVO createMarketOrderDecision(String order, Integer quantity,
			Double triggerPrice, Double price){
		TradeDecisionVO tradeDecisionVO = new TradeDecisionVO();
		tradeDecisionVO.setOrder(order);
		tradeDecisionVO.setOrderType(AdvanceInvestorConstants.MARKET_ORDER);
		tradeDecisionVO.setOrderQuantity(quantity);
		tradeDecisionVO.setPrice(price);
		tradeDecisionVO.setTriggerPrice(triggerPrice);
		return tradeDecisionVO; 
	}
	
	protected TradeDecisionVO createLimitOrderDecision(String order, Integer quantity,
		Double price, Double triggerPrice){
		TradeDecisionVO tradeDecisionVO = new TradeDecisionVO();
		tradeDecisionVO.setOrder(order);
		tradeDecisionVO.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
		tradeDecisionVO.setOrderQuantity(quantity);
		tradeDecisionVO.setPrice(price);
		tradeDecisionVO.setTriggerPrice(triggerPrice);
		return tradeDecisionVO; 
	}
}
