package com.ds.web.crawler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ds.web.crawler.config.ApplicationContextProvider;
import com.ds.web.crawler.config.WebCrawlerConfig;
import com.ds.web.crawler.frontier.Frontier;
import com.ds.web.crawler.index.IndexData;
import com.ds.web.crawler.mongo.entity.CrawledURL;
import com.ds.web.crawler.mongo.entity.CrawledURLBuilder;
import com.ds.web.crawler.parser.HtmlParser;
import com.ds.web.crawler.threads.CrawlerWorker;
import com.ds.web.crawler.threads.ListeningThreadPoolTaskExecutor;
import com.ds.web.crawler.vo.ParsedData;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Class SpringBootController.
 */
@Controller
@SpringBootApplication
@EnableAsync
public class WebCrawlerController {

	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebCrawlerController.class, args);
	}

	@Autowired
	private Frontier frontier;

	@Autowired
	private WebCrawlerConfig webCrawlerConfig;
	
	@Autowired
	@Qualifier("elastic")
	private IndexData indexData;
	

	@RequestMapping(value = "/addSeed", method = RequestMethod.GET)
	@ResponseBody
	public String addSeed(@RequestParam(value = "url") String url) {
		if (StringUtils.isEmpty(url)) {
			frontier.schedule(new CrawledURLBuilder.Builder().setUrl("http://wiprodigital.com/").build());
		} else {
			frontier.schedule(new CrawledURLBuilder.Builder().setUrl(url).build());
		}

		return "URL Added Successfully";
	}

	@RequestMapping("/getUrls")
	@ResponseBody
	public List<CrawledURL> getUrls() {
		return frontier.getUrls(100);
	}

	@RequestMapping(value = "/stopCrawl", method = RequestMethod.GET)
	@ResponseBody
	public String stopCrawl() {
		webCrawlerConfig.setStopCrawl(true);
		return "WebCrawer Stopped Successfully";
	}

	@RequestMapping("/startCrawl")
	@ResponseBody
	public ParsedData startCrawl() {
		CrawlerWorker crawlerWorker = new CrawlerWorker();
		HtmlParser htmlParser = ApplicationContextProvider.getApplicationContext().getBean(HtmlParser.class);
		CrawledURL crawledURL = frontier.getUrls(100).get(0);
		ParsedData htmlParseData = null;
		try {
			crawlerWorker.setCrawledURL(crawledURL);
			crawlerWorker.setFrontier(frontier);
			crawlerWorker.setParser(htmlParser);
			htmlParseData = crawlerWorker.call();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlParseData;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ListeningThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}

	/**
	 * Scheduling runner. Crawling is performed by anywhere from one to
	 * potentially hundreds of threads, each of which loops through the URLs by
	 * taking them from the frontier. These threads may be run in a single
	 * process, or be partitioned amongst multiple processes running at
	 * different nodes of a distributed system. This is just a simple
	 * implementation of spawning controlled threads in a VM. This
	 * implementation shall be even scalable to even run the threads on a
	 * distributed system.
	 * 
	 * @param service
	 *            the service
	 * @return the command line runner
	 */
	@Bean
	public CommandLineRunner schedulingRunner(ListeningThreadPoolTaskExecutor service) {
		return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				while (webCrawlerConfig.isStopCrawl() == false) {
					final HtmlParser htmlParser = ApplicationContextProvider.getApplicationContext()
							.getBean(HtmlParser.class);
					final WebCrawlerConfig config = ApplicationContextProvider.getApplicationContext()
							.getBean(WebCrawlerConfig.class);
					final List<CrawledURL> crawledURLs = frontier.getUrls(webCrawlerConfig.getMaxConcurrentCrawlers());
					for (final CrawledURL crawledURL : crawledURLs) {
						final CrawlerWorker crawlerWorker = new CrawlerWorker();
						crawlerWorker.setCrawledURL(crawledURL);
						crawlerWorker.setParser(htmlParser);
						crawlerWorker.setFrontier(frontier);
						crawlerWorker.setWebCrawlerConfig(config);
						final ListenableFuture<ParsedData> parsedData = (ListenableFuture<ParsedData>) service
								.submit(crawlerWorker);

						Futures.addCallback(parsedData, new FutureCallback<ParsedData>() {
							/*
							 * we want this handler to run immediately
							 */
							public void onSuccess(final ParsedData parsedData) {
								indexData.index(parsedData);
								logger.info("************************* URL Start **********************************");
								logger.info("############# " + parsedData.getUrl() + " #############");
								logger.info(parsedData.toString());
								logger.info("************************* URL End **********************************");
							}

							public void onFailure(final Throwable thrown) {
								logger.error("Exception Crawling", thrown);
							}
						});
					}
				}
			}
		};
	}

}
