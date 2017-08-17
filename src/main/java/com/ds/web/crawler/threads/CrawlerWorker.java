package com.ds.web.crawler.threads;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.exception.WebCrawlerException;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.parser.HtmlParser;
import com.ds.web.crawler.vo.ParsedData;

/**
 * The Class CrawlerWorker. A crawler thread that begins by taking a URL from
 * the frontier and fetching the web page at that URL, generally using the HTTP
 * protocol. The fetched page is then written into a temporary store (which in
 * our case is a MongoDB), where a number of operations are performed on it.
 * Next, the page is parsed and the text as well as the links in it are
 * extracted.
 */
public class CrawlerWorker implements Callable<ParsedData> {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerWorker.class);

	private CrawledURL crawledURL;

	private Frontier frontier;

	private HtmlParser parser;

	private WebCrawlerConfig webCrawlerConfig;

	@Override
	public ParsedData call() throws Exception {
		ParsedData htmlParseData = null;
		final HttpUriRequest request = new HttpGet(this.crawledURL.getURL());
		final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		final CloseableHttpClient httpClient = clientBuilder.build();
		final CloseableHttpResponse response = httpClient.execute(request);

		final HTMLPage htmlPage = new HTMLPage();
		htmlPage.setUrl(this.crawledURL.getURL());
		htmlPage.setFetchResponseHeaders(response.getAllHeaders());
		int statusCode = response.getStatusLine().getStatusCode();
		htmlPage.setStatusCode(statusCode);

		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
				|| statusCode == HttpStatus.SC_MULTIPLE_CHOICES || statusCode == HttpStatus.SC_SEE_OTHER
				|| statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {

			final Header header = response.getFirstHeader("Location");
			if (header != null && webCrawlerConfig.isFollowRedirects()) {
				final String movedToUrl = new URI(header.getValue()).normalize().getPath();
				if (null != frontier.findURL(movedToUrl)) {
					logger.info("This URL is already seen.");
				} else {
					CrawledURL crawledURL = new CrawledURLBuilder.Builder().setUrl(movedToUrl)
							.setParentUrl(this.crawledURL.getURL()).build();
					frontier.schedule(crawledURL);
				}
			}
		}
		if (statusCode >= HttpStatus.SC_OK && statusCode <= 299) {
			try {
				final Document document = Jsoup.connect(crawledURL.getURL()).timeout(3000).get();
				htmlPage.setPageContent(document);
				htmlParseData = parser.parse(htmlPage);
			} catch (IOException e) {
				throw new WebCrawlerException(e);
			}
		}
		return htmlParseData;
	}

	public HtmlParser getParser() {
		return parser;
	}

	public void setParser(HtmlParser parser) {
		this.parser = parser;
	}

	public Frontier getFrontier() {
		return frontier;
	}

	public void setFrontier(Frontier frontier) {
		this.frontier = frontier;
	}

	public CrawledURL getCrawledURL() {
		return crawledURL;
	}

	public void setCrawledURL(CrawledURL crawledURL) {
		this.crawledURL = crawledURL;
	}

	public WebCrawlerConfig getWebCrawlerConfig() {
		return webCrawlerConfig;
	}

	public void setWebCrawlerConfig(WebCrawlerConfig webCrawlerConfig) {
		this.webCrawlerConfig = webCrawlerConfig;
	}

}
