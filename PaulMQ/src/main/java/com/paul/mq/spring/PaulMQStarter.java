package com.paul.mq.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


import com.paul.mq.broker.PaulMQBroker;

public class PaulMQStarter extends PaulMQBroker implements ApplicationContextAware, InitializingBean{
	
	public PaulMQStarter(Integer port) {
		super(port);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
		start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
        System.out.printf("PaulMQ Server Start Success![author paul]\n");		
	}

}
