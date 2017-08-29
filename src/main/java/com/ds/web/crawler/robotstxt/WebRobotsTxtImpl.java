package com.ds.web.crawler.robotstxt;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.exception.WebCrawlerRuntimeException;
import com.panforge.robotstxt.RobotsTxt;

/**
 * The Class WebRobotsTxtImpl.
 */
@Component
public class WebRobotsTxtImpl implements WebRobotsTxt {

	private static final String ROBOTS_TXT = "robots.txt";

	private Map<String, RobotsTxt> cachedBotsMap = new HashMap<>();

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;

	public void init(final String crawledURL) {
		String host = getHost(crawledURL);
		String robotsTxtPath = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(host)) {
			if (host.charAt(host.length() - 1) == '/') {
				robotsTxtPath = String.join("", host, ROBOTS_TXT);
			} else {
				robotsTxtPath = String.join("/", host, ROBOTS_TXT);
			}
		}

		if (null == cachedBotsMap.get(robotsTxtPath)) {
			InputStream urlStream = configureProxy(robotsTxtPath);
			RobotsTxt bots = null;
			try {
				bots = RobotsTxt.read(urlStream);
			} catch (IOException e) {
				throw new WebCrawlerRuntimeException("****** Exception reading robots.txt on URL: " + crawledURL, e);
			}
			cachedBotsMap.put(host, bots);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ds.web.crawler.robotstxt.WebRobotsTxt#allows(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean allows(final String userAgent, final String crawledURL) {
		String host = getHost(crawledURL);
		RobotsTxt bots = cachedBotsMap.get(host);
		if (null == bots) {
			init(crawledURL);
		}
		// Try once again.
		bots = cachedBotsMap.get(host);
		boolean hasAccess = true;
		// If no robots.txt, assume access.
		if (null != bots) {
			if (StringUtils.isEmpty(userAgent)) {
				hasAccess = bots.query("Any", crawledURL);
			} else {
				hasAccess = bots.query(userAgent, crawledURL);
			}
		}
		return hasAccess;
	}

	/**
	 * Gets the host.
	 *
	 * @param crawledURL
	 *            the crawled URL
	 * @return the host
	 */
	private String getHost(String crawledURL) {
		try {
			URL url = new URL(crawledURL);
			return String.join("://", url.getProtocol(), url.getHost().toLowerCase());
		} catch (MalformedURLException e) {
			throw new WebCrawlerRuntimeException("****** Exception resolving Host: " + crawledURL, e);
		}
	}

	/**
	 * Configure proxy if Required.
	 *
	 * @param request
	 *            the request
	 */
	private InputStream configureProxy(final String robotsTxtPath) {
		InputStream urlStream = null;
		try {
			if (webCrawlerConfig.isProxy()) {
				final Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(webCrawlerConfig.getProxyHost(), webCrawlerConfig.getProxyPort()));
				final URL url = new URL(robotsTxtPath);
				final HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
				uc.connect();
				urlStream = uc.getInputStream();
			} else {
				urlStream = new URL(robotsTxtPath).openStream();
			}
		} catch (MalformedURLException e1) {
			throw new WebCrawlerRuntimeException("Malformed URL" + e1);
		} catch (IOException e1) {
			throw new WebCrawlerRuntimeException("****** Check whether Proxy settings are required: " + e1);
		}
		return urlStream;
	}

}
