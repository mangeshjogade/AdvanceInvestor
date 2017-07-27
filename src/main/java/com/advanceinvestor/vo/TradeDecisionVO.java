package com.advanceinvestor.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.advanceinvestor.automatedagent.Agent;
import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.common.CommonUtil;

public class TradeDecisionVO {
	private Date orderId;
	private String order;//SELL,BUY
	/*  -------------------ORDER TYPES----------------------- 
	 * MARKET - current price
	 * LIMIT - equal or better price
	 * STOP_LOSS - cover order where trigger price is below current price and stop loss price is below trigger
	 * 				current price > trigger > stop loss price
	 * BRACKET - 2 Cover orders along with first leg  One for profit booking other for stopping loss     
	 * TRAILING_STOP_LOSS - 2 Cover orders along with first leg  One for profit booking other for stopping loss stop loss will trail as price starts moving desired direction
	 */
	private String orderType;
	private Integer orderQuantity;
	private Double price;
	private Double triggerPrice;
	//price after which stop loss order will be executed
	private Double stopLossPrice;
	//Final price on which you want to sell in case of bracket order 
	private Double bookProfitPrice;
	//final price on which you want to sell
	private Double limitLossTo;
	//Market price OR when price reaches
	// Market will start trailing 
	private String startTrailingLossAt;
	
	private Double startTrailingLossAtPrice;
	
	private Boolean executionCompleted = false;
	
	private Double prevMax =-1.0;
	
	public TradeDecisionVO(){
		setOrderId(new Date());
		setOrder(AdvanceInvestorConstants.NO_ACTION);
		setOrderType(AdvanceInvestorConstants.NO_ACTION);
		setOrderQuantity(0);
		setPrice(0.0);
		setTriggerPrice(0.0);
		setStopLossPrice(0.0);
		setBookProfitPrice(0.0);
		setLimitLossTo(0.0);
		setStartTrailingLossAtPrice(0.0);
	}
	
	public Date getOrderId() {
		return orderId;
	}
	public void setOrderId(Date orderId) {
		this.orderId = orderId;
	}
	public String getOrder() {
		return this.order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getOrderType() {
		return this.orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public Integer getOrderQuantity() {
		return this.orderQuantity;
	}
	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getTriggerPrice() {
		return triggerPrice;
	}
	public void setTriggerPrice(Double triggerPrice) {
		this.triggerPrice = triggerPrice;
	}
	public Double getBookProfitPrice() {
		return bookProfitPrice;
	}
	public void setBookProfitPrice(Double bookProfitPrice) {
		this.bookProfitPrice = bookProfitPrice;
	}
	public Double getLimitLossTo() {
		return limitLossTo;
	}
	public void setLimitLossTo(Double limitLossTo) {
		this.limitLossTo = limitLossTo;
	}
	public String getStartTrailingLossAt() {
		return startTrailingLossAt;
	}
	public void setStartTrailingLossAt(String startTrailingLossAt) {
		this.startTrailingLossAt = startTrailingLossAt;
	}
	public Double getStartTrailingLossAtPrice() {
		return startTrailingLossAtPrice;
	}

	public void setStartTrailingLossAtPrice(Double startTrailingLossAtPrice) {
		this.startTrailingLossAtPrice = startTrailingLossAtPrice;
	}

	public Double getStopLossPrice() {
		return stopLossPrice;
	}
	public void setStopLossPrice(Double stopLossPrice) {
		this.stopLossPrice = stopLossPrice;
	}
	
	public String getOrderIdString(){
		return CommonUtil.getOrderIDStringFormat().format(getOrderId());
	}

	public Boolean isExecutionCompleted() {
		return executionCompleted;
	}

	public Double getPrevMax() {
		return prevMax;
	}

	public void setPrevMax(Double prevMax) {
		this.prevMax = prevMax;
	}

	public void setExecutionCompleted(Boolean executionCompleted) {
		this.executionCompleted = executionCompleted;
	}

	@Override
	public String toString() {
		String retString = null;
		if(getOrder().equals(AdvanceInvestorConstants.BUY)){
			retString =    "***********************Trade Decision************************"+
					"\nID                                  : " + getOrderIdString()  +
					"\nOrder                               : " + getOrder() +
					"\nOrder Type                          : " + getOrderType() +
					"\nOrder Quantity                      : " + getOrderQuantity() +
					"\nOrder Book Profit Price             : " + (getBookProfitPrice() == null ? "N/A":String.format("%.2f",getBookProfitPrice())) +
					"\nOrder Trigger Price                 : " + (getTriggerPrice() == null ? "N/A":String.format("%.2f",getTriggerPrice())) +
					"\nOrder Limit Price                   : " + (getPrice() == null ? "N/A" : String.format("%.2f",getPrice())) +
					"\nOrder Limit Loss To                 : " + (getLimitLossTo() == null ? "N/A":String.format("%.2f",getLimitLossTo())) +
					"\nOrder Stop Loss Price               : " + (getStopLossPrice() == null ? "N/A":String.format("%.2f",getStopLossPrice())) +							  
					"\nOrder Start Trailing Loss At        : " + (getStartTrailingLossAt() == null ? "N/A":getStartTrailingLossAt()) +
					"\nOrder Start Trailing Loss At  Price : " + (getStartTrailingLossAtPrice() == null ? "N/A":String.format("%.2f",getStartTrailingLossAtPrice())) +
					"\n**************************************************************\n";

		}else if (getOrder().equals(AdvanceInvestorConstants.SELL)){
			retString =    "***********************Trade Decision************************"+
					"\nID                                  : " + getOrderIdString()  +
					"\nOrder                               : " + getOrder() +
					"\nOrder Type                          : " + getOrderType() +
					"\nOrder Quantity                      : " + getOrderQuantity() +
					"\nOrder Stop Loss Price               : " + (getStopLossPrice() == null ? "N/A":String.format("%.2f",getStopLossPrice())) +
					"\nOrder Limit Loss To                 : " + (getLimitLossTo() == null ? "N/A":String.format("%.2f",getLimitLossTo())) +
					"\nOrder Limit Price                   : " + (getPrice() == null ? "N/A" : String.format("%.2f",getPrice())) +
					"\nOrder Trigger Price                 : " + (getTriggerPrice() == null ? "N/A":String.format("%.2f",getTriggerPrice())) +
					"\nOrder Book Profit Price             : " + (getBookProfitPrice() == null ? "N/A":String.format("%.2f",getBookProfitPrice())) +
					"\nOrder Start Trailing Loss At        : " + (getStartTrailingLossAt() == null ? "N/A":getStartTrailingLossAt()) +
					"\nOrder Start Trailing Loss At  Price : " + (getStartTrailingLossAtPrice() == null ? "N/A":String.format("%.2f",getStartTrailingLossAtPrice())) +
					"\n**************************************************************\n";

		}
		return retString;
	}
	
	public TradeDecisionVO copy(){
		TradeDecisionVO tradeDecisionVONew = new TradeDecisionVO();
		tradeDecisionVONew.setOrderId(this.getOrderId());
		tradeDecisionVONew.setOrder(this.getOrder());
		tradeDecisionVONew.setOrderType(this.getOrderType());
		tradeDecisionVONew.setOrderQuantity(this.getOrderQuantity());
		tradeDecisionVONew.setPrice(this.getPrice());
		tradeDecisionVONew.setTriggerPrice(this.getTriggerPrice());
		tradeDecisionVONew.setBookProfitPrice(this.getBookProfitPrice());
		tradeDecisionVONew.setLimitLossTo(this.getLimitLossTo());
		tradeDecisionVONew.setStopLossPrice(this.getStopLossPrice());
		tradeDecisionVONew.setStartTrailingLossAt(this.getStartTrailingLossAt());
		tradeDecisionVONew.setExecutionCompleted(this.isExecutionCompleted());
		return tradeDecisionVONew;
	}
	
}
