package com.advanceinvestor.exchange;

import java.util.Map;

import com.advanceinvestor.vo.ExchangeOrderVO;

public interface IExchangeSimulator {

	String placeOrder(ExchangeOrderVO exchangeOrderVO);

	Double getLastPrice();

	Map<String, ExchangeOrderVO> getOrderBookMap();
}
