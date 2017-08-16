/**
 * 
 */
package com.advanceinvestor.automatedagent;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.common.CommonUtil;
import com.advanceinvestor.pricesimulator.IExchangeSimulator;
import com.advanceinvestor.strategy.Strategy;
import com.advanceinvestor.vo.ExchangeOrderVO;
import com.advanceinvestor.vo.TradeDecisionVO;

/**
 * @author Mangesh
 *
 */
public class Agent implements IAgent,Runnable{

	/**
	 * @param args
	 */
	
	private boolean runAgent = true;
	private IExchangeSimulator exchangeSimulator;
	private Map<String,TradeDecisionVO> tslBracketOrderMap = new HashMap<String,TradeDecisionVO>();
	private Map<String,TradeDecisionVO> bracketOrderMap = new HashMap<String,TradeDecisionVO>();
	private Map<String,TradeDecisionVO> orderMap = new HashMap<String,TradeDecisionVO>();
	private Strategy strategy; 
	private Double readPrice;
	private Boolean priceChanged = false;

	public Agent(){
	}
	
	@Override
	public void run() {
		try {
			printTradeSummary();
			while(runAgent){
				tradeExecutorListener();
				Thread.sleep(200);
			}	
			printTradeSummary();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void tradeExecutorListener() throws InterruptedException{
		pocessBrackeOrders();
		pocessTslBrackeOrders();
		if(priceChanged){
			strategy.setCurrentPrice(readPrice);
			generateDecision();
			priceChanged = false;
		}	
		
	}
	
	private void placeExchangeOrder(TradeDecisionVO decision){
		if(isValidOrder(decision) && strategy.isOrderFeasible(decision)){
			ExchangeOrderVO exchangeOrderVO = prepareOrder(decision);
			System.out.println("<----------- Agent Placed Order for below decision ---------->\n");
			System.out.println(decision);
			if(updateExchangeOrderQueue(exchangeOrderVO)){
				strategy.updateMargin(decision);
				strategy.updateOrderCount(decision);
				recordOrder(decision);
			}else{
				System.out.println("<----------- Order was REJECTED by exchange!!! -------------->\n");
			}
		}else{
			System.out.println("#######Invalid Order !!! Please Check your decision !!!####### \n" + decision);				
		}
	}
	
	private boolean isValidOrder(TradeDecisionVO decision) {
		Boolean validation = false;
		
		if(decision.getOrderQuantity() <=0)
			return false;
		
		switch(decision.getOrderType()){
		case AdvanceInvestorConstants.MARKET_ORDER:
			if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
				if(decision.getPrice()>=0.0 && decision.getTriggerPrice() >= 0.0 
						&& decision.getTriggerPrice() <= decision.getPrice())
					validation = true;
			}else if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
				if(decision.getPrice()>=0.0 && decision.getTriggerPrice() >= 0.0 
						&& decision.getTriggerPrice() >= decision.getPrice())
					validation = true;
			}
			break;			
			
		case AdvanceInvestorConstants.LIMIT_ORDER:
			case AdvanceInvestorConstants.BRACKET_ORDER:
				case AdvanceInvestorConstants.BRACKET_TSL_ORDER:
			if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
				if(decision.getPrice()>0.0 && decision.getTriggerPrice() >= 0.0){
					if(decision.getTriggerPrice() > 0.0){
						if(decision.getPrice() >= decision.getTriggerPrice())
							validation = true;
					}else if(decision.getTriggerPrice() == 0.0){
						validation = true;
					}

					if(validation == true && (decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_ORDER) 
							|| decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_TSL_ORDER))){
						if(!(decision.getPrice() < decision.getBookProfitPrice()
								&& decision.getPrice() > decision.getStopLossPrice()
								&& decision.getLimitLossTo() <= decision.getStopLossPrice()
								&& decision.getBookProfitPrice() > decision.getStopLossPrice())){
							validation = false;
						}
					}
				}
			}else if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
				if(decision.getPrice()>0.0 && decision.getTriggerPrice() >= 0.0){
					if(decision.getTriggerPrice() > 0.0){
						if(decision.getPrice() <= decision.getTriggerPrice())
							validation = true;
					}else if(decision.getTriggerPrice() == 0.0){
						validation = true;
					}

					if(validation == true && (decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_ORDER)
							|| decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_TSL_ORDER))){
						if(!(decision.getPrice() > decision.getBookProfitPrice()
								&& decision.getPrice() < decision.getStopLossPrice()
								&& decision.getLimitLossTo() >= decision.getStopLossPrice()
								&& decision.getBookProfitPrice() < decision.getStopLossPrice())){
							validation = false;
						}
					}

				}
			}
			break;
		/*case AdvanceInvestorConstants.STOP_LOSS_ORDER:
			if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
				if(decision.getPrice()>0.0 && decision.getTriggerPrice() > 0.0
						&& decision.getTriggerPrice() <= decision.getPrice()){
					if(decision.getOrder().equals(AdvanceInvestorConstants.BRACKET_ORDER) || decision.getOrder().equals(AdvanceInvestorConstants.BRACKET_TSL_ORDER)){
						if(decision.getPrice() < decision.getBookProfitPrice()
								&& decision.getPrice() > decision.getStopLossPrice()
								&& decision.getLimitLossTo() >= decision.getStopLossPrice()
								&& decision.getBookProfitPrice() > decision.getStopLossPrice()){
							validation = true;
						}
					}else{
						validation = true;
					}
				}
				
			}else if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
				if(decision.getPrice()>0.0 && decision.getTriggerPrice() > 0.0
						&& decision.getTriggerPrice() >= decision.getPrice()){
					if(decision.getOrder().equals(AdvanceInvestorConstants.BRACKET_ORDER) || decision.getOrder().equals(AdvanceInvestorConstants.BRACKET_TSL_ORDER)){
						if(decision.getPrice() > decision.getBookProfitPrice()
								&& decision.getPrice() < decision.getStopLossPrice()
								&& decision.getLimitLossTo() <= decision.getStopLossPrice()
								&& decision.getBookProfitPrice() < decision.getStopLossPrice()){
							validation = true;
						}
					}else{
						validation = true;
					}
				}

			}
			break;	
		case AdvanceInvestorConstants.BRACKET_ORDER:case AdvanceInvestorConstants.BRACKET_TSL_ORDER:
			if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
				if(decision.getPrice()>0.0 
						&& decision.getPrice() < decision.getBookProfitPrice()
						&& decision.getPrice() > decision.getStopLossPrice()
						&& decision.getLimitLossTo() >= decision.getStopLossPrice()
						&& decision.getBookProfitPrice() > decision.getStopLossPrice())
					
					validation = true;
			}else if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
				if(decision.getPrice()>0.0 
						&& decision.getPrice() > decision.getBookProfitPrice()
						&& decision.getPrice() < decision.getStopLossPrice()
						&& decision.getLimitLossTo() <= decision.getStopLossPrice()
						&& decision.getBookProfitPrice() < decision.getStopLossPrice())
					validation = true;
			}
			break;		
*/		default: 
			break;
		}
		
		return validation;
	}

	private void recordOrder(TradeDecisionVO decision) {
		if(decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_ORDER) ){
			bracketOrderMap.put(decision.getOrderIdString(),decision);
		}else if( decision.getOrderType().equals(AdvanceInvestorConstants.BRACKET_TSL_ORDER)){
			tslBracketOrderMap.put(decision.getOrderIdString(),decision);
		}else if (decision.getOrderType().equals(AdvanceInvestorConstants.MARKET_ORDER) || decision.getOrderType().equals(AdvanceInvestorConstants.LIMIT_ORDER) || decision.getOrderType().equals(AdvanceInvestorConstants.STOP_LOSS_ORDER)){
			orderMap.put(decision.getOrderIdString(),decision);
		}
			
	}
	
	private void pocessBrackeOrders() {

		Iterator<String> bracketOrderMapKeysIterator = bracketOrderMap.keySet().iterator();
		while (bracketOrderMapKeysIterator.hasNext()){
			String mapKey =  bracketOrderMapKeysIterator.next();
			TradeDecisionVO decision = bracketOrderMap.get(mapKey);
			boolean orderPlaced = false;
			if(decision.isExecutionCompleted()){
				if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
				
					if (decision.getBookProfitPrice() < exchangeSimulator.getLastPrice()){
						//place book profit order
						System.out.println("<------------------- Book Profit Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getBookProfitPrice());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}else if (decision.getStopLossPrice() > exchangeSimulator.getLastPrice()){
						//place stop loss order
						System.out.println("<--------------------- Stop Loss Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getLimitLossTo());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}
				}if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
					
					if (decision.getBookProfitPrice() > exchangeSimulator.getLastPrice()){
						//place book profit order
						System.out.println("<------------------- Book Profit Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getBookProfitPrice());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}else if (decision.getStopLossPrice() < exchangeSimulator.getLastPrice()){
						//place stop loss order
						System.out.println("<--------------------- Stop Loss Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getLimitLossTo());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}
				}
			}
			if (orderPlaced)
				bracketOrderMap.remove(mapKey);
		}
	}

	
	private void pocessTslBrackeOrders() {

		Iterator<String> bracketOrderMapKeysIterator = tslBracketOrderMap.keySet().iterator();
		while (bracketOrderMapKeysIterator.hasNext()){
			String mapKey =  bracketOrderMapKeysIterator.next();
			TradeDecisionVO decision = tslBracketOrderMap.get(mapKey);
			boolean orderPlaced = false;
			if(decision.isExecutionCompleted()){
				if (decision.getPrevMax() < 0){
					decision.setPrevMax(decision.getPrice());
				}
				if(decision.getOrder().equals(AdvanceInvestorConstants.BUY)){
					Double currPrice = exchangeSimulator.getLastPrice();
					if(decision.getStartTrailingLossAt().equals(AdvanceInvestorConstants.MARKET_PRICE)){
						
						if(decision.getPrevMax() < currPrice){
							Double diff = decision.getPrevMax() - currPrice;
							decision.setLimitLossTo(decision.getLimitLossTo() - diff);
							decision.setStopLossPrice(decision.getStopLossPrice() - diff);
							decision.setPrevMax(currPrice);
						}
					}else if (decision.getStartTrailingLossAt().equals(AdvanceInvestorConstants.WHEN_PRICE_REACHES)){
						if(decision.getStartTrailingLossAtPrice() < currPrice && decision.getPrevMax() < currPrice){
							Double diff = decision.getPrevMax() - currPrice;
							decision.setLimitLossTo(decision.getLimitLossTo() - diff);
							decision.setStopLossPrice(decision.getStopLossPrice() - diff);
							decision.setPrevMax(currPrice);
						}
					}
						
/*					System.out.println(	
					  "\nOrder Limit Loss To                 : " + (decision.getLimitLossTo() == null ? "N/A":String.format("%.2f",decision.getLimitLossTo())) +
					  "\nOrder Stop Loss Price               : " + (decision.getStopLossPrice() == null ? "N/A":String.format("%.2f",decision.getStopLossPrice())) 
					  );*/

					
					if (decision.getBookProfitPrice() < exchangeSimulator.getLastPrice()){
						//place book profit order
						System.out.println("<------------------- Book Profit Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getBookProfitPrice());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}else if (decision.getStopLossPrice() > exchangeSimulator.getLastPrice()){
						//place stop loss order
						System.out.println("<--------------------- Stop Loss Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getLimitLossTo());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}
				}if(decision.getOrder().equals(AdvanceInvestorConstants.SELL)){
					
					Double currPrice = exchangeSimulator.getLastPrice();
					if(decision.getStartTrailingLossAt().equals(AdvanceInvestorConstants.MARKET_PRICE)){
						
						if(decision.getPrevMax() > currPrice){
							Double diff = decision.getPrevMax() - currPrice;
							decision.setLimitLossTo(decision.getLimitLossTo() - diff);
							decision.setStopLossPrice(decision.getStopLossPrice() - diff);
							decision.setPrevMax(currPrice);
						}
					}else if (decision.getStartTrailingLossAt().equals(AdvanceInvestorConstants.WHEN_PRICE_REACHES)){
						if(decision.getStartTrailingLossAtPrice() > currPrice && decision.getPrevMax() > currPrice){
							Double diff = decision.getPrevMax() - currPrice;
							decision.setLimitLossTo(decision.getLimitLossTo() - diff);
							decision.setStopLossPrice(decision.getStopLossPrice() - diff);
							decision.setPrevMax(currPrice);
						}
					}
					
					/*System.out.println(	
							  "\nOrder Limit Loss To                 : " + (decision.getLimitLossTo() == null ? "N/A":String.format("%.2f",decision.getLimitLossTo())) +
							  "\nOrder Stop Loss Price               : " + (decision.getStopLossPrice() == null ? "N/A":String.format("%.2f",decision.getStopLossPrice())) 
							  );*/
					
					if (decision.getBookProfitPrice() > exchangeSimulator.getLastPrice()){
						//place book profit order
						System.out.println("<------------------- Book Profit Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getBookProfitPrice());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}else if (decision.getStopLossPrice() < exchangeSimulator.getLastPrice()){
						//place stop loss order
						System.out.println("<--------------------- Stop Loss Order ---------------------->");
						TradeDecisionVO newBrackerDecision=decision.copy();
						newBrackerDecision.setOrderId(new Date());
						newBrackerDecision.setOrder(getNextTradeOrder(decision.getOrder()));
						newBrackerDecision.setPrice(decision.getLimitLossTo());
						newBrackerDecision.setOrderType(AdvanceInvestorConstants.LIMIT_ORDER);
						newBrackerDecision.setTriggerPrice(decision.getStopLossPrice());
						newBrackerDecision.setBookProfitPrice(0.0);
						newBrackerDecision.setLimitLossTo(0.0);
						newBrackerDecision.setStopLossPrice(0.0);
						newBrackerDecision.setStartTrailingLossAt(null);
						placeExchangeOrder(newBrackerDecision);
						orderPlaced = true;
					}
				}
			}
			if (orderPlaced)
				tslBracketOrderMap.remove(mapKey);
		}
	}
	
	
	
	private ExchangeOrderVO prepareOrder(TradeDecisionVO decision){
		
		ExchangeOrderVO exchangeOrderVO = new ExchangeOrderVO();
		exchangeOrderVO.setOrderID(decision.getOrderIdString());
		exchangeOrderVO.setOrder(decision.getOrder());
		exchangeOrderVO.setOrderQuantity(decision.getOrderQuantity());
		
		
		switch(decision.getOrderType()){
		case AdvanceInvestorConstants.MARKET_ORDER:
			exchangeOrderVO.setPrice(0.0);	
			exchangeOrderVO.setTriggerPrice(decision.getTriggerPrice());
			exchangeOrderVO.setAskingPrice(strategy.getCurrentPrice());
			break;
		case AdvanceInvestorConstants.LIMIT_ORDER:
			case AdvanceInvestorConstants.STOP_LOSS_ORDER:
				case AdvanceInvestorConstants.BRACKET_ORDER:
					case AdvanceInvestorConstants.BRACKET_TSL_ORDER:
			exchangeOrderVO.setPrice(decision.getPrice());	
			exchangeOrderVO.setTriggerPrice(decision.getTriggerPrice());
			exchangeOrderVO.setAskingPrice(decision.getPrice());
			break;
		default: 
			break;
		}
		
		return exchangeOrderVO;
	}
	
	private void generateDecision() {
		TradeDecisionVO decision = strategy.generateDecision();
		if (null != decision && (decision.getOrder().equals(AdvanceInvestorConstants.BUY) || decision.getOrder().equals(AdvanceInvestorConstants.SELL))){
			placeExchangeOrder(decision);
		}	
	}
	
	private boolean updateExchangeOrderQueue(ExchangeOrderVO exchangeOrderVO) {
		String exchangeResponse = exchangeSimulator.orderPlacementCallBack(exchangeOrderVO);
		if(exchangeResponse.equals("SUCCESS"))
			return true;
		return false;
	}


	
	@Override
	public synchronized void tradeConfirmationCallBack(ExchangeOrderVO exchangeOrderConfirmation) {
		System.out.println("############### Exchange Order Confirmation ##################");
		System.out.println(exchangeOrderConfirmation);
		System.out.println("##############################################################\n");
		
		strategy.updateMarginAfterTradeConfirmation(exchangeOrderConfirmation);
		updateOrderConfirmation(exchangeOrderConfirmation.getOrderID());
	}
	

	private void updateOrderConfirmation(String orderIDStr) {
		if(orderMap.containsKey(orderIDStr)){
			orderMap.get(orderIDStr).setExecutionCompleted(true);
		}else if(bracketOrderMap.containsKey(orderIDStr)){
			bracketOrderMap.get(orderIDStr).setExecutionCompleted(true);
		}else if(tslBracketOrderMap.containsKey(orderIDStr)){
			tslBracketOrderMap.get(orderIDStr).setExecutionCompleted(true);
		}
	}

	private String getNextTradeOrder(String order){
		if (order.equals(AdvanceInvestorConstants.SELL))
			return AdvanceInvestorConstants.BUY;
		else if (order.equals(AdvanceInvestorConstants.BUY))
			return AdvanceInvestorConstants.SELL;
		return AdvanceInvestorConstants.NO_ACTION;
	}
	
	public void printTradeSummary(){
		Double lastPrice = exchangeSimulator.getLastPrice();
		Double totalPortfolioValue = strategy.getOrderCount()*lastPrice;
		System.out.println("\n");
		System.out.println("Total Quantity Avaialble       : " + strategy.getOrderCount() +" at "+ CommonUtil.formatDoubleToTwoDecimal(lastPrice));
		System.out.println("Total Margin Avaialble         : " + CommonUtil.formatDoubleToTwoDecimal(strategy.getMargin()));
		System.out.println("Total Portfolio Value (current): " + CommonUtil.formatDoubleToTwoDecimal(totalPortfolioValue));
		System.out.println("Portfolio + Margin             : " + CommonUtil.formatDoubleToTwoDecimal(totalPortfolioValue+strategy.getMargin()));
		System.out.println("\n");
	}
	
	public void stopProcessing(){
		runAgent = false;
	}
	
	public void setExchangeSimulator(IExchangeSimulator exchangeSimulator) {
		this.exchangeSimulator = exchangeSimulator;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public void readPrice(Double readPrice) {
		priceChanged  = true;
		this.readPrice = readPrice;
	}

	
}
