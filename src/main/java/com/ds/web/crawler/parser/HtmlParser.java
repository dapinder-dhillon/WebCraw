package com.ds.web.crawler.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.exception.WebCrawlerRuntimeException;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.frontier.UrlFilter;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.mongo.entity.ParsedData;
import com.google.common.base.Joiner;

@Component("html")
public class HtmlParser implements Parse {

	private static final Logger logger = LoggerFactory.getLogger(HtmlParser.class);

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;

	@Autowired
	private UrlFilter urlFilter;

	@Autowired
	private Frontier frontier;

	private enum HTMLElement {
		AHREF("a[href]"), LINK("link[href]"), SRC("[src]");

		private String value;

		private HTMLElement(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	@Override
	public ParsedData parse(HTMLPage htmlPage) {
		logger.info("************************* Starting Parsing URL: " + htmlPage.getUrl());
		
		Document document = null;
		try {
			document = Jsoup.parse(EntityUtils.toString(htmlPage.getPageContent()));
		} catch (IOException e) {
			throw new WebCrawlerRuntimeException(e);
		}
		final ParsedData parseData = new ParsedData();
		parseData.setUrl(htmlPage.getUrl());
		parseData.setText(document.text());
		parseData.setTitle(document.title());

		final Elements metaAttributes = document.getElementsByTag("meta");
		final Map<String, String> metaTags = new HashMap<>();
		for (Element meta : metaAttributes) {
			metaTags.put(meta.attr("name"), meta.attr("content"));
		}
		parseData.setMetaTags(metaTags);
		logger.info("MetaTags size: " + metaTags.size());

		final Elements media = document.select(HTMLElement.SRC.getValue());
		
		final List<String> images = new ArrayList<>();
		for (final Element src : media) {
			if (src.tagName().equals("img") && urlFilter.match(src.attr("abs:src"))) {
				images.add(src.attr("abs:src"));
			}
		}
		logger.info("No. of images found: " + images.size());
		parseData.setImages(images);

		final Elements allLinksInURl = document.select(HTMLElement.AHREF.getValue());

		final List<String> outGoingURLs = new ArrayList<>();
		for (final Element outGoingUrl : allLinksInURl) {

			final String href = outGoingUrl.attr("abs:href");
			if ((href == null) || href.trim().isEmpty()) {
				continue;
			}

			final String hrefLoweredCase = href.trim().toLowerCase();

			if (StringUtils.isNotEmpty(hrefLoweredCase)) {
				outGoingURLs.add(hrefLoweredCase);
				if (webCrawlerConfig.getMaxOutgoingLinksToFollow() > 0
						&& outGoingURLs.size() > webCrawlerConfig.getMaxOutgoingLinksToFollow()) {
					logger.warn(Joiner.on("-").skipNulls().join("Max URLs Limit exceeded for URL: ", htmlPage.getUrl(),
							" as mentioned in Config"));
					break;
				}
			}

		}
		
		logger.info("No.of  outgoing Links found: " + outGoingURLs.size());
		final Set<CrawledURL> outgoingUrlSet = new HashSet<>();
		if (!CollectionUtils.isEmpty(outGoingURLs)) {
			for (String outgoingURL : outGoingURLs) {
				final CrawledURL crawledURL = new CrawledURLBuilder.Builder().setUrl(outgoingURL)
						.setParentUrl(htmlPage.getUrl()).build();
				outgoingUrlSet.add(crawledURL);
				frontier.schedule(crawledURL);
			}
		}
		parseData.setOutgoingUrls(outgoingUrlSet);
		logger.info("************************* Successfully Parsed: " + htmlPage.getUrl());
		return parseData;
	}

}
