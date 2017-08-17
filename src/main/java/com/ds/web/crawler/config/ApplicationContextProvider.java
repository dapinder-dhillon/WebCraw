package com.ds.web.crawler.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The Class ApplicationContextProvider.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	/** The ApplicationContext context. */
	private static ApplicationContext ctx;

	private ApplicationContextProvider() {
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static <T> T getBean(String name, Class<T> aClass) {
		return ctx.getBean(name, aClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.ctx = arg0;
	}

}
