package com.advanceinvestor.service;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.advanceinvestor.common.CommonUtil;
import com.advanceinvestor.exchange.ExchangeSimulator;
import com.advanceinvestor.vo.ExchangeInstrumentPrice;
import com.advanceinvestor.vo.ExchangeOrderVO;

@Service
@Path("/exchange")
public class AIExchangeService {
	
	@Autowired
	ExchangeSimulator exchangeSimulator; 
	
	@GET
	@Path("/current-price")
	@Produces("application/json")
	public ExchangeInstrumentPrice getCurrentPrice(){
		ExchangeInstrumentPrice exchangeInstrumentPrice = new ExchangeInstrumentPrice();
		exchangeInstrumentPrice.setInstrumentType("EQ");
		exchangeInstrumentPrice.setPrice(exchangeSimulator.getLastPrice());
		return exchangeInstrumentPrice; 
	}
	
	
	@GET
	@Path("/orders/{id}")
	@Produces("application/json")
	public Response getOrderByID(@PathParam("id") String id) {
		ExchangeOrderVO exchangeOrderVO = exchangeSimulator.getOrderBookMap().get(id);
		if (null == exchangeOrderVO) 
			throw new WebApplicationException("Order not Found !!!");
		return Response.ok(exchangeOrderVO).build();
	}
	
	@GET
	@Path("/orders")
	@Produces("application/json")
	public Response getOrders() {
		return Response.ok(exchangeSimulator.getOrderBookMap().values()).build();
	}
	
	@POST
	@Path("/orders")
	@Consumes("application/json")
	public Response createOrder(ExchangeOrderVO exchangeOrderVO) {
		exchangeOrderVO.setOrderID(CommonUtil.getOrderIDStringFormat().format(new Date()));
		String serviceResponse = exchangeSimulator.placeOrder(exchangeOrderVO);
		Response response = serviceResponse.equalsIgnoreCase("SUCCESS")?Response.status(Response.Status.CREATED).build() :Response.status(Response.Status.BAD_REQUEST).build(); 
		return response;
	}
}
