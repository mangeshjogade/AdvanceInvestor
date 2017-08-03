package com.advanceinvestor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.advanceinvestor.service.AIExchangeService;
import com.advanceinvestor.vo.ExchangeInstrumentPrice;

@Controller
public class AIExchangeOrderController {
	
	@Autowired
	AIExchangeService exchangeService;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)	
	public ExchangeInstrumentPrice getLivePrice(){
		return exchangeService.getCurrentPrice();
	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.POST)	
	public ExchangeInstrumentPrice getOrders(){
		return exchangeService.getCurrentPrice();
	}
	
}
