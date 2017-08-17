package com.ds.web.crawler.robotstxt;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.panforge.robotstxt.RobotsTxt;

/**
 * The Class WebRobotsTxtImpl.
 */
@Component
public class WebRobotsTxtImpl implements WebRobotsTxt {

	private Map<String, RobotsTxt> cachedBotsMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ds.web.crawler.robotstxt.WebRobotsTxt#init(java.lang.String)
	 */
	@Override
	public void init(final String crawledURL) {
		String host = getHost(crawledURL);
		String robotsTxtPath = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(host)) {
			if (host.charAt(host.length() - 1) == '/') {
				robotsTxtPath = String.join("", host, "robots.txt");
			} else {
				robotsTxtPath = String.join("/", host, "robots.txt");
			}
		}

		if (null == cachedBotsMap.get(robotsTxtPath)) {
			try (InputStream robotsTxtStream = new URL(robotsTxtPath).openStream()) {
				RobotsTxt bots = RobotsTxt.read(robotsTxtStream);
				cachedBotsMap.put(host, bots);
			} catch (Exception e) {

			}
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

	private String getHost(String crawledURL) {
		try {
			URL url = new URL(crawledURL);
			return String.join("://", url.getProtocol(), url.getHost().toLowerCase());
		} catch (MalformedURLException e) {
		}
		return null;
	}

}
