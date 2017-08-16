package com.advanceinvestor.main;

import java.util.Date;

import com.advanceinvestor.automatedagent.Agent;
import com.advanceinvestor.common.AdvanceInvestorConstants;
import com.advanceinvestor.common.CommonUtil;
import com.advanceinvestor.pricesimulator.ExchangeSimulator;
import com.advanceinvestor.strategy.Portfolio;
import com.advanceinvestor.strategy.Strategy;
import com.advanceinvestor.vo.InvestmentDetailVO;
import com.advanceinvestor.vo.TradeDecisionVO;

public class MainClass {
	

	public static void main(String[] args) throws InterruptedException {
		Strategy strategy = new Strategy();
		Agent agent = new Agent();
		ExchangeSimulator exchangeSimulator = new ExchangeSimulator(101.0);
		exchangeSimulator.getTrendChangeList().add(-8);
		exchangeSimulator.getTrendChangeList().add(-8);
		exchangeSimulator.getTrendChangeList().add(-8);
		exchangeSimulator.getTrendChangeList().add(-8);
		exchangeSimulator.getTrendChangeList().add(-8);
		exchangeSimulator.getTrendChangeList().add(2);
		exchangeSimulator.getTrendChangeList().add(1);
		exchangeSimulator.getTrendChangeList().add(1);
		exchangeSimulator.getTrendChangeList().add(2);
		exchangeSimulator.getTrendChangeList().add(3);
		exchangeSimulator.getTrendChangeList().add(4);
		exchangeSimulator.getTrendChangeList().add(1);
		exchangeSimulator.getTrendChangeList().add(1);
		exchangeSimulator.getTrendChangeList().add(1);
		exchangeSimulator.getTrendChangeList().add(1);
		
		//Setting call Backs 
		agent.setExchangeSimulator(exchangeSimulator);
		exchangeSimulator.setAgent(agent);
		//end
		strategy.setMargin(1000.0);
		agent.setStrategy(strategy);
		//Prepare new investment Details
		InvestmentDetailVO investmentDetailVO = new InvestmentDetailVO();
		investmentDetailVO.setInstrumentType("EQ");
		investmentDetailVO.setName("ABCD");
		investmentDetailVO.setPurchaseDate(new Date());
		investmentDetailVO.setPurchasePrice(95.0);
		investmentDetailVO.setQuantity(10);
		
		Portfolio portfolio = new Portfolio();
		portfolio.getInvestmentList().add(investmentDetailVO);
		
		
		Thread agentTh = new Thread(agent);
		agentTh.start();
		
		Thread exchangeTH = new Thread(exchangeSimulator);
		exchangeTH.start();
		
		int i= 0;
		while(i < 60){
			Double price = exchangeSimulator.getLastPrice();
			//System.out.println("Agent reading price " + CommonUtil.formatDoubleToTwoDecimal(price) + "\n");
			agent.readPrice(price);
			Thread.sleep(1000);
			i++;
		}	
		
		agent.stopProcessing();
		//exchangeSimulator.stopProcessing();
		
		agentTh.join();
		//exchangeTH.join();
	}
}
