package com.ds.web.crawler.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ds.web.crawler.frontier.UrlFilter;

/**
 * The Class WebCrawlerConfig class.
 */
@Configuration
public class WebCrawlerConfig {

	/**
	 * Should we follow redirects?
	 */
	@Value("${crawler.followRedirects:true}")
	private boolean followRedirects;

	/**
	 * Maximum concurrent threads to crawl web.
	 */
	@Value("${crawler.maxConcurrentCrawlers:4}")
	private int maxConcurrentCrawlers;

	/**
	 * Maximum number of pages to fetch. For unlimited number of pages, this
	 * parameter should be set to -1
	 */
	@Value("${crawler.maxOutgoingLinksToFollow:20}")
	private int maxOutgoingLinksToFollow;

	/**
	 * We must avoid repeated fetch requests to a host within a short time span. -1 to disable.
	 */
	//@Value("${crawler.politeness:120000}")
	@Value("${crawler.politeness:-1}")
	private int politeness;

	/** The stop crawl. */
	@Value("${crawler.stopCrawl:false}")
	private boolean stopCrawl;
	
	/** The proxy flag and configuration follows. */
	@Value("${crawler.proxy:true}")
	private boolean proxy;
	
	@Value("${crawler.proxyHost:10.6.13.87}")
	private String proxyHost;
	
	@Value("${crawler.proxyPort:8080}")
	private int proxyPort;
	
	@Value("${crawler.proxyScheme:http}")
	private String proxyScheme;
	
	/** The core pool size. */
	@Value("${crawler.corePoolSize:4}")
	private int corePoolSize;
	
	/** The max pool size. */
	@Value("${crawler.maxPoolSize:10}")
	private int maxPoolSize;

	@Autowired
	private UrlFilter urlFilter;

	@PostConstruct
	public void postMe() {
		urlFilter.addInclude("http://wiprodigital.com/.*");
		urlFilter.addInclude(".*\\.(bmp|gif|jpg|png)$");
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	public int getMaxOutgoingLinksToFollow() {
		return maxOutgoingLinksToFollow;
	}

	public void setMaxOutgoingLinksToFollow(int maxOutgoingLinksToFollow) {
		this.maxOutgoingLinksToFollow = maxOutgoingLinksToFollow;
	}

	public boolean isStopCrawl() {
		return stopCrawl;
	}

	public void setStopCrawl(boolean stopCrawl) {
		this.stopCrawl = stopCrawl;
	}

	public int getMaxConcurrentCrawlers() {
		return maxConcurrentCrawlers;
	}

	public void setMaxConcurrentCrawlers(int maxConcurrentCrawlers) {
		this.maxConcurrentCrawlers = maxConcurrentCrawlers;
	}

	public int getPoliteness() {
		return politeness;
	}

	public void setPoliteness(int politeness) {
		this.politeness = politeness;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyScheme() {
		return proxyScheme;
	}

	public void setProxyScheme(String proxyScheme) {
		this.proxyScheme = proxyScheme;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
}
