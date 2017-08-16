package com.advanceinvestor.pricesimulator;

import com.advanceinvestor.vo.ExchangeOrderVO;

public interface IExchangeSimulator {

	String orderPlacementCallBack(ExchangeOrderVO exchangeOrderVO);

	Double getLastPrice();
}
