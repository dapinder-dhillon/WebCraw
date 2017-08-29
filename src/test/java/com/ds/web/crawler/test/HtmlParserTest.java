package com.ds.web.crawler.test;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.mongo.entity.HTMLPage;
import com.ds.web.crawler.mongo.entity.ParsedData;
import com.ds.web.crawler.parser.Parse;
import com.ds.web.crawler.test.config.TestConfig;
import com.ds.web.crawler.test.config.TestMongoConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, TestMongoConfig.class })
@EnableMongoRepositories(basePackages = "com.ds.web.crawler.mongo.repo")
public class HtmlParserTest {

	@Autowired
	@Qualifier("html")
	private Parse parse;

	@MockBean
	private Frontier frontier;

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;

	@Test
	public void testHTMLParser() {
		Mockito.doNothing().when(frontier).schedule(Matchers.any());

		final String url = "http://wiprodigital.com";
		final HttpGet request = new HttpGet(url);
		final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		final CloseableHttpClient httpClient = clientBuilder.build();

		configureProxy(request);

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			fail("Exception Parsing URL");
		} catch (IOException e) {
			fail("Exception Parsing URL");
		}
		final HTMLPage htmlPage = new HTMLPage();
		htmlPage.setPageContent(response.getEntity());

		htmlPage.setUrl(url);
		final ParsedData parsedData = parse.parse(htmlPage);
		assertNotNull(parsedData);

	}

	/**
	 * Configure proxy if Required.
	 *
	 * @param request
	 *            the request
	 */
	private void configureProxy(final HttpGet request) {
		if (webCrawlerConfig.isProxy()) {
			HttpHost proxy = new HttpHost(webCrawlerConfig.getProxyHost(), webCrawlerConfig.getProxyPort(),
					webCrawlerConfig.getProxyScheme());
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			request.setConfig(config);
		}
	}

}
