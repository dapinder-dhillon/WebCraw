package com.ds.web.crawler.test;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.ParsedData;
import com.ds.web.crawler.parser.ParserFactory;
import com.ds.web.crawler.test.config.TestConfig;
import com.ds.web.crawler.test.config.TestMongoConfig;
import com.ds.web.crawler.threads.CrawlerWorker;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, TestMongoConfig.class })
@EnableMongoRepositories(basePackages = "com.ds.web.crawler.mongo.repo")
public class CrawlerWorkerTest {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerWorkerTest.class);

	@MockBean
	private Frontier frontier;
	
	@Autowired
	private WebCrawlerConfig webCrawlerConfig;
	
	@Autowired
	private ParserFactory parserFactory;

	@Test
	public void startCrawl() {
		final List<CrawledURL> mockedList = new ArrayList<>();
		
		
		final CrawledURL mockedURL1 = new CrawledURL();
		mockedURL1.setURL("http://wiprodigital.com/");
		
		final CrawledURL mockedURL2 = new CrawledURL();
		mockedURL2.setURL("http://wiprodigital.com/news/new-survey-highlights-leadership-crisis-digital-transformation");
		
		final CrawledURL mockedURL3 = new CrawledURL();
		mockedURL3.setURL("http://wiprodigital.com/what-we-do#work-three-circles-row");
		
		final CrawledURL mockedURL4 = new CrawledURL();
		mockedURL4.setURL("http://wiprodigital.com/2017/08/08/rise-mobile-banking-defining-success-customer-led-revolution/");
		
		mockedList.add(mockedURL1);
		mockedList.add(mockedURL2);
		mockedList.add(mockedURL3);
		mockedList.add(mockedURL4);
		
		
		when(frontier.getUrls(Matchers.anyInt())).thenReturn(mockedList);
		
		Mockito.doNothing().when(frontier).schedule(Matchers.any());
		
		CrawlerWorker crawlerWorker = new CrawlerWorker();
		List<CrawledURL> crawledURLList = frontier.getUrls(100);
		ParsedData htmlParseData = null;
		try {
			for(CrawledURL crawledURL :crawledURLList){
				crawlerWorker.setCrawledURL(crawledURL);
				crawlerWorker.setFrontier(frontier);
				crawlerWorker.setParserFactory(parserFactory);
				crawlerWorker.setWebCrawlerConfig(webCrawlerConfig);
				htmlParseData = crawlerWorker.call();
				assertNotNull(htmlParseData);
				logger.info("##### Parsed Data: " + htmlParseData.toString());	
			}
		} catch (Exception e) {
			fail("Exception Adding Seed");
		}
	}
}
