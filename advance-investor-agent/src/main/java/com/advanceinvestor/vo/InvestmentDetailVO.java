package com.advanceinvestor.vo;

import java.util.Date;

import com.advanceinvestor.common.CommonUtil;

public class InvestmentDetailVO {
	private String instrumentType;
	private String name;
	private Integer quantity;
	private Double purchasePrice;
	private Date purchaseDate;
	
	@Override
	public String toString() {
		String retString =      
				    "Instrument Type      : " + getInstrumentType() +    
				  "\nName                 : " + getName() +
				  "\nQuantity             : " + getQuantity()+
				  "\nPurchase Price       : " + (getPurchasePrice() == null? "N/A" : String.format("%.2f",getPurchasePrice())) +
				  "\nPurchase Date        : " + (getPurchaseDate() == null? "N/A" : CommonUtil.getOrderIDStringFormat().format(getPurchaseDate()));
		return retString;
	}
	
	public String getInstrumentType() {
		return instrumentType;
	}
	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	
}
