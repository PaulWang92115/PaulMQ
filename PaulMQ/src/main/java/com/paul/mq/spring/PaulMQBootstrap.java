package com.paul.mq.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PaulMQBootstrap {

	public static final String CONFIG = "classpath:com/paul/mq/spring/broker-server.xml";
	
	public void start(){
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		context.start();
	}
	
	public static void main(String[] args){
		PaulMQBootstrap bootstrap = new PaulMQBootstrap();
		bootstrap.start();
	}
}
