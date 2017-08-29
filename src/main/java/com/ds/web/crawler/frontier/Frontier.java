package com.ds.web.crawler.frontier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.exception.WebCrawlerException;
import com.ds.web.crawler.exception.WebCrawlerRuntimeException;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.repo.CrawledURLRepository;
import com.ds.web.crawler.robotstxt.WebRobotsTxt;
import com.google.common.base.Joiner;

/**
 * The URL frontier maintains the URLs in the frontier and regurgitates them in
 * some order whenever a crawler thread seeks a URL. This needs to be further
 * improved since a Frontier shall be much more than currently it is.
 * 
 * Two important considerations govern the order in which URLs are returned by
 * the frontier.
 * 
 * First, high-quality pages that change frequently should be prioritized for
 * frequent crawling.
 * 
 * The second consideration is politeness: we must avoid repeated fetch requests
 * to a host within a short time span
 */
@Component
public class Frontier {

	private static final Logger logger = LoggerFactory.getLogger(Frontier.class);
	
	private Object mutex = new Object();

	private Map<String, Long> politenessHostMap = new HashMap<>();

	@Autowired
	private CrawledURLRepository crawledURLRepository;

	@Autowired
	private UrlFilter urlFilter;

	@Autowired
	private URLCanonical urlCanonical;

	@Autowired
	private WebRobotsTxt robotsTxt;

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;

	public void schedule(CrawledURL crawledURL) {
		String canonicalUrl = null;
		try {
			canonicalUrl = urlCanonical.get(crawledURL.getURL());
		} catch (WebCrawlerException e) {
			throw new WebCrawlerRuntimeException(e);
		}

		synchronized (mutex) {
			final CrawledURL crawledUrl = crawledURLRepository.findByUrl(canonicalUrl);
			if (null != crawledUrl) {
				logger.info("URL Already seen: " + canonicalUrl);
			} else {
				boolean urlFilterFlag = urlFilter.match(canonicalUrl);
				if (urlFilterFlag) {
					logger.info(Joiner.on(StringUtils.EMPTY).join("URL: '", canonicalUrl,
							"' ALLOWED as per Configured Filter"));
				} else {
					logger.info(Joiner.on(StringUtils.EMPTY).join("URL: '", canonicalUrl,
							"' NOT_ALLOWED as per Configured Filter"));
				}

				if (urlFilterFlag) {
					boolean robotFlag = robotsTxt.allows(null, canonicalUrl);
					logger.info(Joiner.on(" | ").join("User Agent on Robots.txt is \"", robotFlag, "\" for URL: ", canonicalUrl));
					if (robotFlag) {
						crawledURLRepository.save(crawledURL);
					}
				}
			}
		}
	}

	/**
	 * Two important considerations govern the order in which URLs are returned
	 * by the frontier.
	 * 
	 * First, high-quality pages that change frequently should be prioritized
	 * for frequent crawling. This is a TODO.
	 * 
	 * The second consideration is politeness: we must avoid repeated fetch
	 * requests to a host within a short time span.
	 *
	 * @param max
	 *            the max
	 * @return the urls
	 */
	public List<CrawledURL> getUrls(int max) {
		List<CrawledURL> politeCrawledURLs = Collections.emptyList();
		/*final CrawledURL query = new CrawledURL();
		query.setSeen(true);
		final ExampleMatcher matcher = ExampleMatcher.matching();
		final Example<CrawledURL> exampleMatcher = Example.of(query, matcher);*/
		final List<CrawledURL> crawledURLs = crawledURLRepository.findAll();
		
		if (!CollectionUtils.isEmpty(crawledURLs)) {
			politeCrawledURLs = new ArrayList<>();
			if (webCrawlerConfig.getPoliteness() > 0) {
				for (final CrawledURL crawledUrl : crawledURLs) {
					final String host = getHost(crawledUrl.getURL());
					final Long lastCrawledTime = this.politenessHostMap.get(host);
					if (null == lastCrawledTime) {
						politeCrawledURLs.add(crawledUrl);
						this.politenessHostMap.put(host, System.currentTimeMillis());
					} else {
						boolean politenessFlag = ((System.currentTimeMillis() - lastCrawledTime) > webCrawlerConfig
								.getPoliteness());
						if (politenessFlag) {
							politeCrawledURLs.add(crawledUrl);
						} else {
							logger.info("Politeness Delay for Host: " + host);
						}
					}
				}
			} else {
				int i = 1;
				for (final CrawledURL crawledUrl : crawledURLs) {
					if (crawledUrl.isSeen() == false) {
						politeCrawledURLs.add(crawledUrl);
						crawledUrl.setSeen(true);
						crawledURLRepository.save(crawledUrl);
						// crawledURLRepository.delete(crawledUrl);
						if (i == max) {
							break;
						}
					}
				}
			}
		}
		// crawledURLRepository.deleteAll();
		return politeCrawledURLs;
	}

	/**
	 * Find URL.
	 *
	 * @param url
	 *            the url
	 * @return the crawled URL
	 */
	public CrawledURL findURL(String url) {
		return crawledURLRepository.findByUrl(url);
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
			return url.getHost().toLowerCase();
		} catch (MalformedURLException e) {
		}
		return null;
	}
}
