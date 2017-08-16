package com.advanceinvestor.vo;

public class ExchangeOrderVO {
	private String order;
	private Integer orderQuantity;
	private Double executionPrice;
	private Double price;
	private Double triggerPrice;
	
	private Double askingPrice;//this field is to track the price at which order was placed
	
	private String orderID;
	
	@Override
	public String toString() {
		String retString =      "Order ID                   : " + getOrderID() +    
							  "\nOrder                      : " + getOrder() +
							  "\nOrder Quantity             : " + getOrderQuantity()+
							  "\nOrder Price                : " + (getPrice() == null? "N/A" : String.format("%.2f",getPrice())) +
							  "\nOrder Trigger Price        : " + (getTriggerPrice() == null? "N/A" : String.format("%.2f",getTriggerPrice())) + 
							  "\nAsking Price               : " + (getAskingPrice() == null? "N/A" : String.format("%.2f",getAskingPrice())) +
							  "\nExchange Execution Price   : " + (getExecutionPrice() == null ? "N/A" : String.format("%.2f",getExecutionPrice()));
		return retString;
	}
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public Double getExecutionPrice() {
		return executionPrice;
	}

	public void setExecutionPrice(Double executionPrice) {
		this.executionPrice = executionPrice;
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

	public Double getAskingPrice() {
		return askingPrice;
	}

	public void setAskingPrice(Double askingPrice) {
		this.askingPrice = askingPrice;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	
}
