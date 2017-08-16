package com.advanceinvestor.common;

import java.text.SimpleDateFormat;

public class CommonUtil {
	
	private static SimpleDateFormat orderIDStringFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	public static String formatDoubleToTwoDecimal(Double value){
		return String.format("%.2f",value);
	}
	
	public static SimpleDateFormat getOrderIDStringFormat() {
		return orderIDStringFormat;
	}
}
