package com.advanceinvestor.automatedagent;

import com.advanceinvestor.vo.ExchangeOrderVO;

public interface IAgent {
	public void tradeConfirmationCallBack(ExchangeOrderVO exchangeOrderConfirmation);
}
