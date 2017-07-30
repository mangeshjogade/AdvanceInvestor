package com.advanceinvestor.exchange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.vo.ExchangeOrderVO;

@Component
public class ExchangeSimulator implements IExchangeSimulator{
	private Double basePrice;
	private Double lastPrice;
	private Integer maxChangeRange;
	private Integer heartBeat = 1;
	private int trendChangeCounter = 0;
	
	private List<Integer> trendChangeList = new ArrayList<Integer>();
	
	private List<ExchangeOrderVO> rowOrderList = new LinkedList<ExchangeOrderVO>();
	private PriorityQueue<ExchangeOrderVO> marketOrderQueue = new PriorityQueue<ExchangeOrderVO>(new OrderQComparator());
	private PriorityQueue<ExchangeOrderVO> buyLimitOrderQueue = new PriorityQueue<ExchangeOrderVO>(new OrderQComparator());

	private PriorityQueue<ExchangeOrderVO> sellLimitOrderQueue = new PriorityQueue<ExchangeOrderVO>(new OrderQComparator());

	public ExchangeSimulator() throws InterruptedException{
		setBasePrice(100.0);
		calculateRangeForChange(basePrice);
		setLastPrice(generateRandomPrice(getBasePrice()));
		trendChangeList.add(1);
		trendChangeList.add(3);
		trendChangeList.add(4);
		trendChangeList.add(3);
		trendChangeList.add(8);
		trendChangeList.add(9);
		trendChangeList.add(3);
		trendChangeList.add(2);
		trendChangeList.add(1);
		trendChangeList.add(-1);
		trendChangeList.add(-3);
		trendChangeList.add(-3);
		trendChangeList.add(-4);
		trendChangeList.add(-6);
		trendChangeList.add(-8);
	}

	 @Scheduled(fixedRate = 200)
	public void run() {
				generatetNextPrice();
				processTradeQueue();
				if(heartBeat > 10000) heartBeat =1; 
				else heartBeat++;
	}
	
	private void calculateRangeForChange(Double basePrice){
		if(basePrice % 10 >= 5 ){
			basePrice = (basePrice - basePrice % 10) + 10.0;  
		}else{
			basePrice = (basePrice - basePrice % 10);
		}
		
		//System.out.println(basePrice);
		Double maxRangeDouble = (basePrice * 0.02);
		setMaxChangeRange(maxRangeDouble.intValue());
	}
	
	private Double generatetNextPrice(){
		double nextPrice = generateRandomPrice(getLastPrice());
		setLastPrice(nextPrice);
		//System.out.println(String.format("%.2f",nextPrice));
		return nextPrice;
	}
	
	private Double generateRandomPrice(Double startingPrice){
		//System.out.println("Starting Price : "+startingPrice);
		Double sign = ((new Random().nextInt(10))%3)==0?-1.0:1.0;
		//System.out.println("Sign : "+ sign);
		Double randomDouble = getRandomDouble();
		Double intmdChange = 0.0;
		if(heartBeat % 5 == 0){
			//System.out.println("heartBeat : "+heartBeat);
			//System.out.println("trendChangeCounter : "+trendChangeCounter);
			if (trendChangeCounter < trendChangeList.size()){
					intmdChange = 0.05 * trendChangeList.get(trendChangeCounter);
				trendChangeCounter++;
			}	
			else
				trendChangeCounter = 0;
			return startingPrice + intmdChange;
		}	
		//System.out.println(randomDouble);
		//System.out.println(randomDouble*sign);
		//System.out.println(startingPrice);
		return startingPrice + (randomDouble*sign);
	}

	private Double getRandomDouble(){
		Random ran = new Random();
		Integer nextNum = ran.nextInt(getMaxChangeRange());
		//System.out.println(nextNum);
		Double newDiff = nextNum * 5.0;
		//System.out.println(newDiff/100);
		return newDiff/100;
	}
	
	private synchronized void processTradeQueue(){
		boolean itemPlacedFlag = false;
		//Process Row Q to update Limit and
		Iterator<ExchangeOrderVO> rowOrderListIterator = rowOrderList.iterator();
		while(rowOrderListIterator.hasNext()){
			ExchangeOrderVO exchangeOrderVO = rowOrderListIterator.next();
			if (exchangeOrderVO.getPrice() == 0.0 &&  exchangeOrderVO.getTriggerPrice() == 0.0){
				marketOrderQueue.add(exchangeOrderVO);
				itemPlacedFlag = true;
			}else if (exchangeOrderVO.getOrder().equals(AdvanceInvestorConstants.BUY)){
				if (exchangeOrderVO.getPrice() > 0.0 && exchangeOrderVO.getTriggerPrice() == 0.0){
					buyLimitOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}else if (exchangeOrderVO.getPrice() == 0.0 && exchangeOrderVO.getTriggerPrice() < getLastPrice()){
					marketOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}else if(exchangeOrderVO.getTriggerPrice() < getLastPrice()){
					buyLimitOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}
			}else if (exchangeOrderVO.getOrder().equals(AdvanceInvestorConstants.SELL)){
				if (exchangeOrderVO.getPrice() > 0.0 && exchangeOrderVO.getTriggerPrice() == 0.0){
					sellLimitOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}else if (exchangeOrderVO.getPrice() == 0.0 && exchangeOrderVO.getTriggerPrice() > getLastPrice()){
					marketOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}else if(exchangeOrderVO.getTriggerPrice() > getLastPrice()){
					sellLimitOrderQueue.add(exchangeOrderVO);
					itemPlacedFlag = true;
				}
			}
			
			if(itemPlacedFlag)
				rowOrderListIterator.remove();
		}

		//process Market Order Q		
		while(marketOrderQueue.size() > 0){
			ExchangeOrderVO headOrder = marketOrderQueue.poll();
			headOrder.setExecutionPrice(getLastPrice());
			//agent.tradeConfirmationCallBack(headOrder);
		}
		
		//process Buy Limit Order Q 
		while(buyLimitOrderQueue.size() > 0 ){
			ExchangeOrderVO headOrder = buyLimitOrderQueue.peek();
			if(headOrder.getPrice() >= getLastPrice()){
				headOrder = buyLimitOrderQueue.poll();
				headOrder.setExecutionPrice(getLastPrice());
				//agent.tradeConfirmationCallBack(headOrder);
			}else{
				break;
			}
		}
		//process Se Limit Order Q 
		while(sellLimitOrderQueue.size() > 0 ){
			ExchangeOrderVO headOrder = sellLimitOrderQueue.peek();
			if(headOrder.getPrice() <= getLastPrice()){
				headOrder = sellLimitOrderQueue.poll();
				headOrder.setExecutionPrice(getLastPrice());
				//agent.tradeConfirmationCallBack(headOrder);
			}else{
				break;
			}
		}
	}
	
	@Override
	public synchronized String orderPlacementCallBack(ExchangeOrderVO exchangeOrderVO) {
		System.out.println("---------------------Exchange Received Order------------------");
		System.out.println(exchangeOrderVO);
		System.out.println("--------------------------------------------------------------\n");
		if(orderFormatCorrect(exchangeOrderVO)){
			rowOrderList.add(exchangeOrderVO);
			return "SUCCESS";
		}	
		return "FAILURE";
	}
	
	private boolean orderFormatCorrect(ExchangeOrderVO exchangeOrderVO) {
		if(null!=exchangeOrderVO.getOrder()
		&& null!=exchangeOrderVO.getOrderQuantity()
		&& null!=exchangeOrderVO.getPrice()
		&& null!=exchangeOrderVO.getTriggerPrice())
			return true;
		return false;
	}

	private Double getBasePrice() {
		return basePrice;
	}

	private void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}
	
	private Integer getMaxChangeRange() {
		return maxChangeRange;
	}

	private void setMaxChangeRange(Integer maxChangeRange) {
		this.maxChangeRange = maxChangeRange;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	private void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}


	public void setTrendChangeList(List<Integer> trendChangeList) {
		this.trendChangeList = trendChangeList;
	}
	
	public List<Integer> getTrendChangeList() {
		return this.trendChangeList;
	}


	class OrderQComparator implements Comparator<ExchangeOrderVO>{

		@Override
		public int compare(ExchangeOrderVO o1, ExchangeOrderVO o2) {
			if (o1.getPrice() > o2.getPrice())
				return 1;
			else if (o1.getPrice() < o2.getPrice())
				return -1;
			return 0;
		}
	}
}


