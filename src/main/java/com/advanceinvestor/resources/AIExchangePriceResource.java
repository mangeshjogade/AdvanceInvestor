package com.advanceinvestor.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.advanceinvestor.service.AIExchangeService;
import com.advanceinvestor.vo.ExchangeInstrumentPrice;

@RestController
@RequestMapping("/exchange")
public class AIExchangePriceResource {
	
	@Autowired
	AIExchangeService exchangeService;
	
	@RequestMapping(value = "/current-price", method = RequestMethod.GET)
	public ExchangeInstrumentPrice getCurretntPrice(){
		return exchangeService.getCurrentPrice();
	}
}
