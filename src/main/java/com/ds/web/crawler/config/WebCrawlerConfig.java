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
	@Value("${crawler.maxConcurrentCrawlers:1}")
	private int maxConcurrentCrawlers;

	/**
	 * Maximum number of pages to fetch For unlimited number of pages, this
	 * parameter should be set to -1
	 */
	@Value("${crawler.maxOutgoingLinksToFollow:1}")
	private int maxOutgoingLinksToFollow;

	/**
	 * We must avoid repeated fetch requests to a host within a short time span.
	 */
	@Value("${crawler.politeness:120000}")
	//@Value("${crawler.politeness:1}")
	private int politeness;

	@Value("${crawler.stopCrawl:false}")
	private boolean stopCrawl;

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
}
