package com.advanceinvestor.vo;

import java.util.LinkedList;
import java.util.List;

public class SessionDataVO {
	//data points
	private List<Double> priceHistory = new LinkedList<Double>();
	private Double sessionHigh = 0.0;
	private Double sessionLow = 0.0;
	private Double sessionAverage = 0.0;
	private Double SessionPercentageChangeFromStart = 0.0;
	private Double SessionCounter = 0.0;
	private Double SessionSum = 0.0;
	//End
}
