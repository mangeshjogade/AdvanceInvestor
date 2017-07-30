package com.advanceinvestor.exchange;

import com.advanceinvestor.vo.ExchangeOrderVO;

public interface IExchangeSimulator {

	String orderPlacementCallBack(ExchangeOrderVO exchangeOrderVO);

	Double getLastPrice();
}
