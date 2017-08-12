package com.advanceinvestor;

import javax.ws.rs.ext.Provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.advanceinvestor.exceptionmapper.ExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;

@SpringBootApplication
@EnableScheduling
public class AIExchangeSimulator {

	public static void main(String[] args) {
		SpringApplication.run(AIExchangeSimulator.class, args);
	}
	
	@Bean
	public JacksonJaxbJsonProvider jsonProvider(ObjectMapper objectMapper) {
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(objectMapper);
		return provider;
	}
	
	@Bean
	public ExceptionHandler globalExceptionHandler() {
		return new ExceptionHandler();
	}
	
}