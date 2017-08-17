package com.ds.web.crawler.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.frontier.UrlFilter;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.vo.ParsedData;

@Component
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

		Document document = htmlPage.getPageContent();
		final ParsedData parseData = new ParsedData();
		parseData.setUrl(htmlPage.getUrl());
		parseData.setText(document.text());
		parseData.setTitle(document.title());

		Elements metaAttributes = document.getElementsByTag("meta");
		final Map<String, String> metaTags = new HashMap<>();
		for (Element meta : metaAttributes) {
			metaTags.put(meta.attr("name"), meta.attr("content"));
		}
		parseData.setMetaTags(metaTags);

		Elements links = document.select(HTMLElement.AHREF.getValue());
		Elements media = document.select(HTMLElement.SRC.getValue());

		logger.info("Images: ", media.size());
		final List<String> images = new ArrayList<>();
		for (final Element src : media) {
			if (src.tagName().equals("img") && urlFilter.match(src.attr("abs:src"))) {
				images.add(src.attr("abs:src"));
			}
		}
		parseData.setImages(images);

		logger.info("Links: ", links.size());
		final Elements outGoingUrls = document.select(HTMLElement.AHREF.getValue());

		final List<String> outgoingURLs = new ArrayList<>();
		for (final Element outGoingUrl : outGoingUrls) {

			final String href = outGoingUrl.attr("abs:href");
			if ((href == null) || href.trim().isEmpty()) {
				continue;
			}

			final String hrefLoweredCase = href.trim().toLowerCase();

			if (StringUtils.isNotEmpty(hrefLoweredCase)) {
				outgoingURLs.add(hrefLoweredCase);
				if (outgoingURLs.size() > webCrawlerConfig.getMaxOutgoingLinksToFollow()) {
					logger.warn("Max URLs Limit exceeded as mentioned in Config");
				}

			}

		}
		final Set<CrawledURL> outgoingUrlSet = new HashSet<>();
		if (!CollectionUtils.isEmpty(outgoingURLs)) {
			for (String outgoingURL : outgoingURLs) {
				final CrawledURL crawledURL = new CrawledURLBuilder.Builder().setUrl(outgoingURL)
						.setParentUrl(htmlPage.getUrl()).build();
				outgoingUrlSet.add(crawledURL);
				frontier.schedule(crawledURL);
			}
		}
		parseData.setOutgoingUrls(outgoingUrlSet);
		return parseData;
	}

}
