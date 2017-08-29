package com.ds.web.crawler.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.ds.web.crawler.WebCrawlerController;
import com.ds.web.crawler.config.CustomWebMvcConfiguration;
import com.ds.web.crawler.config.InitalizeThreadPoolExecutor;

@Configuration
@ComponentScan(basePackages = {
		"com.ds.web.crawler" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
				InitalizeThreadPoolExecutor.class, WebCrawlerController.class, CustomWebMvcConfiguration.class }))
public class TestConfig {

}
