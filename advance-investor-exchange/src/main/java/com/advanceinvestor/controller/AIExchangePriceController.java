package com.advanceinvestor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.advanceinvestor.service.AIExchangeService;
import com.advanceinvestor.vo.ExchangeInstrumentPrice;

@Controller
public class AIExchangePriceController {
	
	@Autowired
	AIExchangeService exchangeService;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@SendTo("/topic/current-price")
	public ExchangeInstrumentPrice getLivePrice(){
		return exchangeService.getCurrentPrice();
	}
}
