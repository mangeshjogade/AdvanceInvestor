package com.advanceinvestor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.advanceinvestor.exchange.ExchangeSimulator;
import com.advanceinvestor.vo.ExchangeInstrumentPrice;

@Service
public class AIExchangeService {
	
	@Autowired
	ExchangeSimulator exchangeSimulator; 
	
	public ExchangeInstrumentPrice getCurrentPrice(){
		ExchangeInstrumentPrice exchangeInstrumentPrice = new ExchangeInstrumentPrice();
		exchangeInstrumentPrice.setInstrumentType("EQ");
		exchangeInstrumentPrice.setPrice(exchangeSimulator.getLastPrice());
		return exchangeInstrumentPrice; 
	}
	
	
	
}
