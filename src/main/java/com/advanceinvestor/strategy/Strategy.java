package com.advanceinvestor.strategy;

import java.util.LinkedList;
import java.util.List;

import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.common.CommonUtil;
import com.advanceinvestor.vo.ExchangeOrderVO;
import com.advanceinvestor.vo.TradeDecisionVO;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

public class Strategy {
	private Double margin;
	private Integer orderCount = 0 ;
	private Double currentPrice;
	private Portfolio portfolio;
	private String currentStrategy;
	private InsureStrategy insureStrategy= new InsureStrategy(20.0, 0.5);
	//TA Lib class 
	Core function = new Core();
	//End
	
	//data points
	private List<Double> priceHistory = new LinkedList<Double>();
	private Double sessionHigh = 0.0;
	private Double sessionLow = 0.0;
	private Double sessionAverage = 0.0;
	private Double SessionPercentageChangeFromStart = 0.0;
	private Double SessionCounter = 0.0;
	private Double SessionSum = 0.0;
	//End
	
	public Boolean isOrderFeasible(TradeDecisionVO decision){
		if (decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
			if(orderCount<decision.getOrderQuantity()){
				System.out.println("Insufficiant quantity available to Sell !!! Hence selling short !!!");
			}
		}
		if (decision.getOrder().equals(AdvanceInvestorConstants.BUY) || decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
			if (margin < decision.getOrderQuantity() * decision.getPrice() )
				System.out.println("Insufficiant Margin available!!!, Available : "+ CommonUtil.formatDoubleToTwoDecimal(margin) + ", Asking Margin :" + CommonUtil.formatDoubleToTwoDecimal(decision.getOrderQuantity() * decision.getPrice()));
			else
				return true;
		}
		System.out.println("Order not feasible!!!");
		return false;
	}
	
	
	public void updateMargin(TradeDecisionVO decision){
		
		switch(decision.getOrderType()){
		case AdvanceInvestorConstants.MARKET_ORDER:
			if (decision.getOrder() == AdvanceInvestorConstants.BUY){
				setMargin(getMargin() - decision.getOrderQuantity() * currentPrice);
			}else if (decision.getOrder() == AdvanceInvestorConstants.SELL){
				setMargin(getMargin() + decision.getOrderQuantity() * currentPrice);
			}
			break;
		case AdvanceInvestorConstants.LIMIT_ORDER: case AdvanceInvestorConstants.BRACKET_ORDER: case AdvanceInvestorConstants.BRACKET_TSL_ORDER: case AdvanceInvestorConstants.STOP_LOSS_ORDER:
			if (decision.getOrder() == AdvanceInvestorConstants.BUY){
				setMargin(getMargin() - decision.getOrderQuantity() * decision.getPrice());
			}else if (decision.getOrder() == AdvanceInvestorConstants.SELL){
				setMargin(getMargin() + decision.getOrderQuantity() * decision.getPrice());
			}
			break;
		default: 
			break;
		}	
	}
	
	public void updateOrderCount(TradeDecisionVO decision){
		if (decision.getOrder() == AdvanceInvestorConstants.BUY){
			orderCount += decision.getOrderQuantity();
		}else if (decision.getOrder() == AdvanceInvestorConstants.SELL){
			orderCount -= decision.getOrderQuantity();
		}
	}
	
	public void updateMarginAfterTradeConfirmation(ExchangeOrderVO exchangeOrderConfirmation){
		Double priceAdjustmentAfterTrade = (exchangeOrderConfirmation.getAskingPrice() 
				- exchangeOrderConfirmation.getExecutionPrice()) * exchangeOrderConfirmation.getOrderQuantity();
		if(exchangeOrderConfirmation.getOrder().equals(AdvanceInvestorConstants.BUY)){
			setMargin(getMargin() + priceAdjustmentAfterTrade);
			System.out.println("Adjusted Margin by : " + CommonUtil.formatDoubleToTwoDecimal(priceAdjustmentAfterTrade)+",Total now : "+ CommonUtil.formatDoubleToTwoDecimal(getMargin())+"\n");
		}else if(exchangeOrderConfirmation.getOrder().equals(AdvanceInvestorConstants.SELL)){
			setMargin(getMargin() - priceAdjustmentAfterTrade);
			System.out.println("Adjusted Margin by : " + CommonUtil.formatDoubleToTwoDecimal(priceAdjustmentAfterTrade*-1) +",Total now : "+ CommonUtil.formatDoubleToTwoDecimal(getMargin())+"\n");
		}
	}
	
	public TradeDecisionVO generateDecision() {
		processData();
		TradeDecisionVO tradeDecisionVO = null;
		switch (currentStrategy){
			case "RETAIN" :
				tradeDecisionVO = insureStrategy.returnDecision(currentPrice, orderCount, 10, 100.0);
				break;
		}
		return tradeDecisionVO;
	}
	
	private void processData(){
		currentStrategy = "RETAIN";
	}
	
	private Double returnPercentageValue(Double value,Double percentage){
		return value + (value * percentage);
	}
	
	
	
	private void generateDataPoints(){
		if (SessionCounter == 0.0){
			sessionHigh = currentPrice;
			sessionLow = currentPrice;
		}
		
		priceHistory.add(currentPrice);
		//calculating session High
		if(sessionHigh < currentPrice)
			sessionHigh = currentPrice;
		//calculating session Low 
		else if(sessionLow > currentPrice)
			sessionLow = currentPrice;
		SessionCounter++;
		//calculating average 
		SessionSum +=currentPrice;
		sessionAverage = SessionSum/SessionCounter;
		
		SessionPercentageChangeFromStart = ((currentPrice - priceHistory.get(0))/priceHistory.get(0)) * 100;
		
		//Calculate SMA 20 
		if(SessionCounter > 20){
		    int startIdx = 0;
		    int endIdx = priceHistory.size()-1;
			double[] inReal = priceHistory.stream().mapToDouble( i -> i).toArray();
		    int optInTimePeriod = 20;
		    MInteger outBegIdx = new MInteger(); 
		    MInteger outNBElement = new MInteger();
		    double[] outReal = new double[priceHistory.size()];
		    
		    //SMA
			//function.sma(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal);
			//RSI
			function.rsi(startIdx, endIdx, inReal, optInTimePeriod, outBegIdx, outNBElement, outReal);
			System.out.println(String.format("%.2f",outReal[outNBElement.value-1]));
		}
		//printDataPoints();
	}
	
	private void printDataPoints(){
		System.out.println(
				"Data Points                        " +
				"\nsessionHigh                      : "+String.format("%.2f",sessionHigh) +
				"\nsessionLow                       : "+String.format("%.2f",sessionLow) +
				"\nsessionAverage                   : "+String.format("%.2f",sessionAverage) +
				"\nSessionPercentageChangeFromStart : "+String.format("%.2f",SessionPercentageChangeFromStart) + "%\n"
				);
	}
	
	public Double getMargin() {
		return margin;
	}


	public void setMargin(Double margin) {
		this.margin = margin;
	}


	public Integer getOrderCount() {
		return orderCount;
	}


	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}


	public Double getCurrentPrice() {
		return currentPrice;
	}


	public void setCurrentPrice(Double currentPrice) {
		//generateDataPoints();
		this.currentPrice = currentPrice;
	}


	public Portfolio getPortfolio() {
		return portfolio;
	}


	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
}
