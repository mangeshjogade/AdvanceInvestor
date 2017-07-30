package com.advanceinvestor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AIExchangeMessageService {
	
	@Autowired
	AIExchangeService exchangeService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	
	@Scheduled(fixedRate = 1000)
	public void writeLivePrice(){
		//ExchangeInstrumentPrice exchangeInstrumentPrice = exchangeService.getCurrentPrice();
		//System.out.println(exchangeInstrumentPrice);
		//return exchangeService.getCurrentPrice();
		
		simpMessagingTemplate.convertAndSend("/topic/live-price",exchangeService.getCurrentPrice());
	}
}
